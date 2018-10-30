grammar NewGrammar;

@header {
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
}

@members {
int cnt = 0;
String termName = "TERM";
NonTerminal start;
Map<String, Terminal> terminals = new HashMap<>();
Map<String, NonTerminal> nonTerminals = new HashMap<>();

String removeBraces(String input) {
    return input.substring(1, input.length() - 1).trim();
}
}

start returns [String title, String header, String members]
    : ('grammar' grammarName=TOKEN)? (HEADER h=CODE)? (MEMBERS m=CODE)? parseRules {
        $title = ($grammarName.text == null) ? "" : $grammarName.text;
        $header = ($h.text == null) ? "" : removeBraces($h.text);
        $members = ($m.text == null) ? "" : removeBraces($m.text);
    }
    ;

parseRules
    //nonTerminal
    : nt=nonTerminalName (arguments[$nt.elem])? (RETURNS returnsBlock[$nt.elem])? (init[$nt.elem])?
        COLON parseAlternatives[$nt.elem] SEMICOLON parseRules
    //terminal
    | t=terminalName COLON terminalDescription[$t.elem] SEMICOLON parseRules
    | EOF
;

arguments[Element elem]
    : LBrack
        v=readArgument {
            $elem.addArgument($v.type, $v.name);
        }
        (Comma v=readArgument{ $elem.addArgument($v.type, $v.name); })*? RBrack
;

returnsBlock[Element elem]
    : LBrack v=readArgument { $elem.setReturnAttr($v.type, $v.name); } RBrack
;

readArgument returns [String type, String name]
    : t=(TOKEN|RULE) n=(TOKEN|RULE) {
        $type = $t.text;
        $name = $n.text;
    }
;

init[NonTerminal nt]
    : INIT i=CODE {
        $nt.setInit(($i.text == null) ? "" : removeBraces($i.text));
    }
;

nonTerminalName returns [NonTerminal elem]
    : r=RULE {
        String name = $r.text;
        if (nonTerminals.containsKey(name)) {
            $elem = nonTerminals.get(name);
        } else {
            $elem = new NonTerminal(name);
            if (nonTerminals.isEmpty()) {
                start = $elem;
            }
            nonTerminals.put(name, $elem);
        }
    }
;

terminalName returns [Terminal elem]
    : t=TOKEN {
        String name = $t.text;
        if (terminals.containsKey(name)) {
            $elem = terminals.get(name);
        } else {
            $elem = new Terminal(name);
            terminals.put(name, $elem);
        }
    }
;

terminalDescription[Terminal elem]
    // string
    : es=ANY (VBAR ds=terminalDescription[$elem])? {
        $elem.addString(removeBraces($es.text));
    }
    // regexp
    | es=REGEXP (VBAR ds=terminalDescription[$elem])? {
        $elem.addRegex($es.text);
    }
    // token reference
    | es=TOKEN (VBAR ds=terminalDescription[$elem])? {
        if (terminals.containsKey($es.text)) {
            Terminal t = terminals.get($es.text);
            $elem.addStrings(t.strings);
            $elem.addAllRegex(t.regex);
        }
    }
;

parseAlternatives[NonTerminal elem]
    : expressions[$elem, new Rule()] (VBAR parseAlternatives[$elem])?
;

expressions[NonTerminal elem, Rule rule]
    : expression[rule] expressions[$elem, $rule]
    | expression[rule] {
        $elem.addRule(rule);
    }
;

expression[Rule rule]
    : nt=nonTerminalName LBrack vs=argsNames RBrack (c=CODE)? {
        $rule.add($nt.elem, $vs.args, $c.text == null ? null : removeBraces($c.text));
    }
    | nt=nonTerminalName (c=CODE)? {
        $rule.add($nt.elem, null, $c.text == null ? null : removeBraces($c.text));
    }
    | t=readTerminal (c=CODE)? {
        $rule.add($t.elem, null, $c.text == null ? null : removeBraces($c.text));
    }
;

argsNames returns [List<String> args]
    @init {
        $args = new ArrayList<>();
    }
    : v=argName Comma vs=argsNames {
        $args.add($v.name);
        $args.addAll($vs.args);
    }
    | v=argName {
        $args.add($v.name);
    }
;

argName returns [String name]
    : n=(TOKEN|RULE) {
        $name=$n.text;
    }
    | n=CODE {
        $name = removeBraces($n.text);
    }
;

readTerminal returns [Terminal elem]
    : name=terminalName {
        $elem = $name.elem;
    }
    | t=REGEXP {
        String name = termName.concat(Integer.toString(cnt++));
        $elem = new Terminal(name);
        $elem.addRegex($t.text);
        terminals.put(name, $elem);
    }
    | t=ANY {
        String name = termName.concat(Integer.toString(cnt++));
        $elem = new Terminal(name);
        $elem.addString(removeBraces($t.text));
        terminals.put(name, $elem);
    }
;

VBAR          : '|' ;
COLON         : ':' ;
SEMICOLON     : ';' ;
OPAREN        : '(' ;
CPAREN        : ')' ;
LBrack        : '[' ;
RBrack        : ']' ;
QUESTION      : '?' ;
PLUS          : '+' ;
ASTERISK      : '*' ;
Comma         : ',' ;

RETURNS       : 'returns' ;
HEADER        : '@header' ;
MEMBERS       : '@members' ;
INIT          : '@init' ;

RULE          : [a-z_][a-zA-Z0-9_]* ;
TOKEN         : [A-Z][a-zA-Z0-9_]* ;

ANY           : '\''.+?'\'';
CODE          : '{'.+?'}';
REGEXP        : '/'.+?'/' ;

WS            : [\r\n\t ]+ -> skip ;
