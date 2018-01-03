/**
 * Copyright 2017-2018 Philip Helger, pgcc@helger.com
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
package com.helger.pgcc.jjdoc;

import com.helger.commons.ValueEnforcer;
import com.helger.pgcc.parser.CodeProductionCpp;
import com.helger.pgcc.parser.CodeProductionJava;
import com.helger.pgcc.parser.Expansion;
import com.helger.pgcc.parser.ExpNonTerminal;
import com.helger.pgcc.parser.NormalProduction;
import com.helger.pgcc.parser.AbstractExpRegularExpression;
import com.helger.pgcc.parser.TokenProduction;

/**
 * A report generator for a grammar.
 *
 * @author timp
 * @since 11-Dec-2006
 */
public interface IDocGenerator
{
  /**
   * Output string with entity substitution for brackets and ampersands.
   *
   * @param s
   *        the String to output
   */
  void text (String s);

  /**
   * Output String.
   *
   * @param s
   *        String to output
   */
  void print (String s);

  /**
   * Output document header.
   */
  void documentStart ();

  /**
   * Output document footer.
   */
  void documentEnd ();

  /**
   * Output Special Tokens.
   *
   * @param s
   *        tokens to output
   */
  void specialTokens (String s);

  void handleTokenProduction (TokenProduction tp);

  // /**
  // * Output start of a TokenProduction.
  // * @param tp the TokenProduction being output
  // */
  // void tokenStart(TokenProduction tp);
  //
  // /**
  // * Output end of a TokenProduction.
  // * @param tp the TokenProduction being output
  // */
  // void tokenEnd(TokenProduction tp);

  /**
   * Output start of non-terminal.
   */
  void nonterminalsStart ();

  /**
   * Output end of non-terminal.
   */
  void nonterminalsEnd ();

  /**
   * Output start of tokens.
   */
  void tokensStart ();

  /**
   * Output end of tokens.
   */
  void tokensEnd ();

  /**
   * Output comment from a production.
   *
   * @param jp
   *        the JavaCodeProduction to output
   */
  void javacode (CodeProductionJava jp);

  /**
   * Output comment from a production.
   *
   * @param cp
   *        the CppCodeProduction to output
   */
  void cppcode (CodeProductionCpp cp);

  /**
   * Output start of a normal production.
   *
   * @param np
   *        the NormalProduction being output
   */
  void productionStart (NormalProduction np);

  /**
   * Output end of a normal production.
   *
   * @param np
   *        the NormalProduction being output
   */
  void productionEnd (NormalProduction np);

  /**
   * Output start of an Expansion.
   *
   * @param e
   *        Expansion being output
   * @param first
   *        whether this is the first expansion
   */
  void expansionStart (Expansion e, boolean first);

  /**
   * Output end of Expansion.
   *
   * @param e
   *        Expansion being output
   * @param first
   *        whether this is the first expansion
   */
  void expansionEnd (Expansion e, boolean first);

  /**
   * Output start of non-terminal.
   *
   * @param nt
   *        the NonTerminal being output
   */
  void nonTerminalStart (ExpNonTerminal nt);

  /**
   * Output end of non-terminal.
   *
   * @param nt
   *        the NonTerminal being output
   */
  void nonTerminalEnd (ExpNonTerminal nt);

  /**
   * Output start of regular expression.
   *
   * @param re
   *        the RegularExpression being output
   */
  void reStart (AbstractExpRegularExpression re);

  /**
   * Output end of regular expression.
   *
   * @param re
   *        the RegularExpression being output
   */
  void reEnd (AbstractExpRegularExpression re);

  /**
   * Log debug messages.
   *
   * @param message
   *        the string to log
   */
  default void debug (final String message)
  {
    System.err.println (message);
  }

  /**
   * Log informational messages.
   *
   * @param message
   *        the string to log
   */
  default void info (final String message)
  {
    System.err.println (message);
  }

  /**
   * Log warning messages.
   *
   * @param message
   *        the string to log
   */
  default void warn (final String message)
  {
    System.err.println (message);
  }

  /**
   * Log error messages.
   *
   * @param message
   *        the string to log
   */
  default void error (final String message)
  {
    System.err.println (message);
  }

  /**
   * Dummy method to ensure parameters are used...
   *
   * @param o
   *        anything
   */
  default void doNothing (final Object o)
  {
    ValueEnforcer.notNull (o, "any");
  }
}
