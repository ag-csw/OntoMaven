Case G. A circular importation with two titled texts - each imported text imports the other.
Both are imported into the main text, one without restriction and one inside a domain restriction.

Includes that create circularity are commented out.

The importation in the titling statement is not removed because the assignment of a name to a text
must not be changed in the importation resolution process.

The directory /input holds the original corpus, with importations.
The directory /result holds a logically-equivalent corpus where all importations that are resolvable have been rewritten
in terms of XInclude statements.

We assume that all files at the top level (directly under /input or /result) constitute the corpus.
