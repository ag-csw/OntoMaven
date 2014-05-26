Case D. A circular importation - the imported text imports itself.

The nested include is commented out because it would create circularity.
The importation in the titling statement is not removed because the assignment of a name to a text
must not be changed in the importation resolution process.

The directory /input holds the original corpus, with importations.
The directory /result holds a logically-equivalent corpus where all importations that are resolvable have been rewritten
in terms of XInclude statements.

We assume that all files at the top level (directly under /input or /result) constitute the corpus.
