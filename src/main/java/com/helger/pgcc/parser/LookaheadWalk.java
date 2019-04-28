/**
 * Copyright 2017-2019 Philip Helger, pgcc@helger.com
 *
 * Copyright 2011 Google Inc. All Rights Reserved.
 * Author: sreeni@google.com (Sreeni Viswanadha)
 *
 * Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.helger.pgcc.parser;

import java.util.ArrayList;
import java.util.List;

public final class LookaheadWalk
{
  static boolean s_considerSemanticLA;
  static List <MatchInfo> s_sizeLimitedMatches;

  private LookaheadWalk ()
  {}

  public static List <MatchInfo> genFirstSet (final List <MatchInfo> partialMatches, final Expansion exp)
  {
    if (exp instanceof AbstractExpRegularExpression)
    {
      final List <MatchInfo> retval = new ArrayList <> ();
      for (int i = 0; i < partialMatches.size (); i++)
      {
        final MatchInfo m = partialMatches.get (i);
        final MatchInfo mnew = new MatchInfo ();
        for (int j = 0; j < m.m_firstFreeLoc; j++)
        {
          mnew.m_match[j] = m.m_match[j];
        }
        mnew.m_firstFreeLoc = m.m_firstFreeLoc;
        mnew.m_match[mnew.m_firstFreeLoc++] = ((AbstractExpRegularExpression) exp).m_ordinal;
        if (mnew.m_firstFreeLoc == MatchInfo.s_laLimit)
        {
          s_sizeLimitedMatches.add (mnew);
        }
        else
        {
          retval.add (mnew);
        }
      }
      return retval;
    }

    if (exp instanceof ExpNonTerminal)
    {
      final NormalProduction prod = ((ExpNonTerminal) exp).getProd ();
      if (prod instanceof AbstractCodeProduction)
      {
        return new ArrayList <> ();
      }
      return genFirstSet (partialMatches, prod.getExpansion ());
    }

    if (exp instanceof ExpChoice)
    {
      final List <MatchInfo> retval = new ArrayList <> ();
      final ExpChoice ch = (ExpChoice) exp;
      for (int i = 0; i < ch.getChoices ().size (); i++)
      {
        final List <MatchInfo> v = genFirstSet (partialMatches, ch.getChoices ().get (i));
        retval.addAll (v);
      }
      return retval;
    }

    if (exp instanceof ExpSequence)
    {
      List <MatchInfo> v = partialMatches;
      final ExpSequence seq = (ExpSequence) exp;
      for (int i = 0; i < seq.m_units.size (); i++)
      {
        v = genFirstSet (v, seq.m_units.get (i));
        if (v.size () == 0)
          break;
      }
      return v;
    }

    if (exp instanceof ExpOneOrMore)
    {
      final List <MatchInfo> retval = new ArrayList <> ();
      List <MatchInfo> v = partialMatches;
      final ExpOneOrMore om = (ExpOneOrMore) exp;
      while (true)
      {
        v = genFirstSet (v, om.m_expansion);
        if (v.size () == 0)
          break;
        retval.addAll (v);
      }
      return retval;
    }

    if (exp instanceof ExpZeroOrMore)
    {
      final List <MatchInfo> retval = new ArrayList <> (partialMatches);
      List <MatchInfo> v = partialMatches;
      final ExpZeroOrMore zm = (ExpZeroOrMore) exp;
      while (true)
      {
        v = genFirstSet (v, zm.m_expansion);
        if (v.size () == 0)
          break;
        retval.addAll (v);
      }
      return retval;
    }

    if (exp instanceof ExpZeroOrOne)
    {
      final List <MatchInfo> retval = new ArrayList <> ();
      retval.addAll (partialMatches);
      retval.addAll (genFirstSet (partialMatches, ((ExpZeroOrOne) exp).m_expansion));
      return retval;
    }

    if (exp instanceof ExpTryBlock)
    {
      return genFirstSet (partialMatches, ((ExpTryBlock) exp).m_exp);
    }

    if (s_considerSemanticLA && exp instanceof ExpLookahead && ((ExpLookahead) exp).getActionTokens ().isNotEmpty ())
    {
      return new ArrayList <> ();
    }

    final List <MatchInfo> retval = new ArrayList <> (partialMatches);
    return retval;
  }

  private static void _listSplit (final List <MatchInfo> toSplit,
                                  final List <MatchInfo> mask,
                                  final List <MatchInfo> partInMask,
                                  final List <MatchInfo> rest)
  {
    OuterLoop: for (int i = 0; i < toSplit.size (); i++)
    {
      for (int j = 0; j < mask.size (); j++)
      {
        if (toSplit.get (i) == mask.get (j))
        {
          partInMask.add (toSplit.get (i));
          continue OuterLoop;
        }
      }
      rest.add (toSplit.get (i));
    }
  }

  public static List <MatchInfo> genFollowSet (final List <MatchInfo> partialMatches,
                                               final Expansion exp,
                                               final long generation)
  {
    if (exp.m_myGeneration == generation)
    {
      return new ArrayList <> ();
    }
    // System.out.println("*** Parent: " + exp.parent);
    exp.m_myGeneration = generation;
    if (exp.m_parent == null)
    {
      final List <MatchInfo> retval = new ArrayList <> (partialMatches);
      return retval;
    }

    if (exp.m_parent instanceof NormalProduction)
    {
      final List <Expansion> parents = ((NormalProduction) exp.m_parent).getParents ();
      final List <MatchInfo> retval = new ArrayList <> ();
      // System.out.println("1; gen: " + generation + "; exp: " + exp);
      for (int i = 0; i < parents.size (); i++)
      {
        final List <MatchInfo> v = genFollowSet (partialMatches, parents.get (i), generation);
        retval.addAll (v);
      }
      return retval;
    }

    if (exp.m_parent instanceof ExpSequence)
    {
      final ExpSequence seq = (ExpSequence) exp.m_parent;
      List <MatchInfo> v = partialMatches;
      for (int i = exp.m_ordinalBase + 1; i < seq.m_units.size (); i++)
      {
        v = genFirstSet (v, seq.m_units.get (i));
        if (v.size () == 0)
          return v;
      }
      List <MatchInfo> v1 = new ArrayList <> ();
      List <MatchInfo> v2 = new ArrayList <> ();
      _listSplit (v, partialMatches, v1, v2);
      if (v1.size () != 0)
      {
        // System.out.println("2; gen: " + generation + "; exp: " + exp);
        v1 = genFollowSet (v1, seq, generation);
      }
      if (v2.size () != 0)
      {
        // System.out.println("3; gen: " + generation + "; exp: " + exp);
        v2 = genFollowSet (v2, seq, Expansion.getNextGenerationIndex ());
      }
      v2.addAll (v1);
      return v2;
    }

    if (exp.m_parent instanceof ExpOneOrMore || exp.m_parent instanceof ExpZeroOrMore)
    {
      final List <MatchInfo> moreMatches = new ArrayList <> (partialMatches);
      List <MatchInfo> v = partialMatches;
      while (true)
      {
        v = genFirstSet (v, exp);
        if (v.size () == 0)
          break;
        moreMatches.addAll (v);
      }
      List <MatchInfo> v1 = new ArrayList <> ();
      List <MatchInfo> v2 = new ArrayList <> ();
      _listSplit (moreMatches, partialMatches, v1, v2);
      if (v1.size () != 0)
      {
        // System.out.println("4; gen: " + generation + "; exp: " + exp);
        v1 = genFollowSet (v1, (Expansion) exp.m_parent, generation);
      }
      if (v2.size () != 0)
      {
        // System.out.println("5; gen: " + generation + "; exp: " + exp);
        v2 = genFollowSet (v2, (Expansion) exp.m_parent, Expansion.getNextGenerationIndex ());
      }
      v2.addAll (v1);
      return v2;
    }

    // System.out.println("6; gen: " + generation + "; exp: " + exp);
    return genFollowSet (partialMatches, (Expansion) exp.m_parent, generation);
  }

  public static void reInit ()
  {
    s_considerSemanticLA = false;
    s_sizeLimitedMatches = null;
  }
}
