Case M. Multiple titlings with the same name of texts that are syntactically different, but are the same at the level of xml-model.
None of these should not produce an inconsistency.

The directory /input holds the original corpus, with importations.
The directory /result holds a logically-equivalent corpus where all importations that are resolvable have been rewritten
in terms of XInclude statements.

We assume that all files at the top level (directly under /input or /result) constitute the corpus.
