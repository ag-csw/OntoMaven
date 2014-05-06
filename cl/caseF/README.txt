Case F. A circular importation with two titled texts - each imported text imports the other.
Both are imported into the main text without restriction.

Includes that create circularity are commented out.
There are multiple ways to do this
- /result1 shows a "breadth first" approach 
- /result2 shows a "depth first" approach

They are logically equivalent, and I have no preference in implementation. 

The importation in the titling statement is not removed because the assignment of a name to a text
must not be changed in the importation resolution process.

The directory /input holds the original corpus, with importations.
The directory /result holds a logically-equivalent corpus where all importations that are resolvable have been rewritten
in terms of XInclude statements.

We assume that all files at the top level (directly under /input or /result) constitute the corpus.
