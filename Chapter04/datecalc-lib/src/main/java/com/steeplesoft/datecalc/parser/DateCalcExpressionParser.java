/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.datecalc.parser;

import com.steeplesoft.datecalc.DateCalcException;
import com.steeplesoft.datecalc.parser.token.DateToken;
import com.steeplesoft.datecalc.parser.token.IntegerToken;
import com.steeplesoft.datecalc.parser.token.OperatorToken;
import com.steeplesoft.datecalc.parser.token.TimeToken;
import com.steeplesoft.datecalc.parser.token.Token;
import com.steeplesoft.datecalc.parser.token.UnitOfMeasureToken;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jason
 */
public class DateCalcExpressionParser {

    private final List<InfoWrapper> infos = new ArrayList<>();

    public DateCalcExpressionParser() {
        addTokenInfo(new DateToken.Info());
        addTokenInfo(new TimeToken.Info());
        addTokenInfo(new IntegerToken.Info());
        addTokenInfo(new OperatorToken.Info());
        addTokenInfo(new UnitOfMeasureToken.Info());
    }

    public Queue<Token> parse(String text) {
        final Queue<Token> tokens = new ArrayDeque<>();

        if (text != null) {
            text = text.trim();
            if (!text.isEmpty()) {
                boolean matchFound = false;
                for (InfoWrapper iw : infos) {
                    final Matcher matcher = iw.pattern.matcher(text);
                    if (matcher.find()) {
                        matchFound = true;
                        String match = matcher.group().trim();
                        tokens.add(iw.info.getToken(match));
                        tokens.addAll(parse(text.substring(match.length())));
                        break;
                    }
                }
                if (!matchFound) {
                    throw new DateCalcException("Could not parse the expression: " + text);
                }
            }
        }

        return tokens;
    }

    private void addTokenInfo(Token.Info info) {
        infos.add(new InfoWrapper(info));
    }

    private class InfoWrapper {

        Token.Info info;
        Pattern pattern;

        InfoWrapper(Token.Info info) {
            this.info = info;
            pattern = Pattern.compile("^(" + info.getRegex() + ")");
        }
    }
}
