grammar TableComment;
comment: localName? ( ':' detail )? EOF;
localName: TEXT;
detail: description? ( ':' taglist )?;
taglist: tag ( ':' tag )*;
tag: TEXT;
description: TEXT;
WS : [ \t\r\n]+ -> skip ; // skip tabs, newlines
TEXT: (~':')+;
