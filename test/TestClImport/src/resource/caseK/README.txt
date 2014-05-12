Case K. Importation of nested titlings.
This is a complex case that requires recursion because a nested titling only becomes active after it has been imported.
It is conceivable to have multiple nested titlings but only one is active because its outer titling has been imported.
It is not possible to tell from the titling file alone which one of the nested titlings (if any) will be active.

The directory /input holds the original corpus, with importations.
The directory /result holds a logically-equivalent corpus where all importations that are resolvable have been rewritten
in terms of XInclude statements.

We assume that all files at the top level (directly under /input or /result) constitute the corpus.
