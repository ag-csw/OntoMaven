Case J. Importation of a title that is not defined.

The importation statement is not changed if the imported title is not defined in the corpus, in case
the corpus is merged with another where the title is defined.
A warning should be logged at an appropriate logging level.
Semantically, the importation is treated like a comment.

The directory /input holds the original corpus, with importations.
The directory /result holds a logically-equivalent corpus where all importations that are resolvable have been rewritten
in terms of XInclude statements.

We assume that all files at the top level (directly under /input or /result) constitute the corpus.
