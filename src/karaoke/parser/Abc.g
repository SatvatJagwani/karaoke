// Grammar for ABC music notation 
abc_tune ::= abc_header abc_body;


abc_header ::= field_number comment* field_title other_fields* field_key;

@skip space_or_tab{
    field_number ::= "X:" index end_of_line;
    field_title ::= "T:" text end_of_line;
    other_fields ::= field_composer | field_default_length | field_meter | field_tempo | field_voice | comment;
    field_composer ::= "C:" text end_of_line;
    field_default_length ::= "L:" note_length_strict end_of_line;
    field_meter ::= "M:" meter end_of_line;
    field_tempo ::= "Q:" tempo end_of_line;
    field_voice ::= "V:" text end_of_line;
    field_key ::= "K:" key end_of_line;
}

key ::= keynote mode_minor?;
keynote ::= basenote key_accidental?;
key_accidental ::= "#" | "b";
mode_minor ::= "m";

meter ::= "C" | "C|" | meter_fraction;
meter_fraction ::= digit+ "/" digit+;

tempo ::= meter_fraction "=" digit+;
text ::= [^\n\r]*;
note_length_strict ::= digit+ "/" digit+;
index ::= digit+;


abc_body ::= abc_line+;
abc_line ::= element+ end_of_line (lyric end_of_line)?  | middle_of_body_field | comment;
element ::= note_element | rest_element | tuplet_element | barline | nth_repeat | space_or_tab; 

note_element ::= note | chord;

note ::= pitch note_length;
pitch ::= accidental? basenote octave?;
octave ::= "'"+ | ","+;
note_length ::= (digit+)? ("/" (digit+)?)?;

accidental ::= "^" | "^^" | "_" | "__" | "=";

basenote ::= "C" | "D" | "E" | "F" | "G" | "A" | "B" | "c" | "d" | "e" | "f" | "g" | "a" | "b";

rest_element ::= "z" note_length;

tuplet_element ::= tuplet_spec note_element+;
tuplet_spec ::= "(" digit; 

chord ::= "[" note+ "]";

barline ::= "|" | "||" | "[|" | "|]" | ":|" | "|:";
nth_repeat ::= "[1" | "[2";

middle_of_body_field ::= field_voice;

lyric ::= "w:" lyrical_element*;
lyrical_element ::= "*" | "|" | word | space;
word ::= chunk (separator chunk)* underscores;
separator ::= space? hyphens underscores;
hyphens ::= "-"+;
underscores ::= "_"*;
space ::= " "+;
chunk ::= multiple_syllables | multiple_words;
multiple_words ::= lyric_text (tilde lyric_text)*;
multiple_syllables ::= lyric_text (backslash_hyphen lyric_text)*;
tilde ::= "~";
backslash_hyphen ::= "\\" "-";
lyric_text ::= [^\n\r*|\-_~ \\]*;             //[a-zA-Z\".?!,';]*;

comment ::= space_or_tab* "%" comment_text newline;
comment_text ::= [^\n\r]*;

end_of_line ::= comment | newline;

digit ::= [0-9];
newline ::= "\n"| "\r" "\n"?;
space_or_tab ::= " " | "\t";