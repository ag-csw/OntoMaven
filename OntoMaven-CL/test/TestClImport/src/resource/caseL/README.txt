Case L. Importation of nested titlings requiring multiple passes through files.
This case shows that a corpus can have multiple files (in addition to the titling file) and the importations
in these files can be interrelated.

Assuming the files are processed in lexicographic order according to title, the importation in the
second file cannot be resolved until after the importation in the third file is resolved,
because it depends on a nested titling that is only activated by the importation in the third file.

The directory /input holds the original corpus, with importations.
The directory /result holds a logically-equivalent corpus where all importations that are resolvable have been rewritten
in terms of XInclude statements.

We assume that all files at the top level (directly under /input or /result) constitute the corpus.