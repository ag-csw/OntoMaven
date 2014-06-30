Case S. A corpus assembled entirely from "remote" texts that includes a circular importation.

result1: shows an approach where titled XInclude directives are not changed directly, but use catalog.xml to
         redirect the "remote" URL to a path into the includes directory.
         This approach has the advantage of maintaining a more complete record of sources, through the catalog redirects.

result2: shows an approach where titled XInclude directives are modified, so that catalog.xml is used only
         for redirections arising from importation resolution.
         There is no longer any reference to the external URL through the catalog.
         A workaround to this would be to retain the original XInclude directive as an XML comment (as shown in the results).
         
* Both approaches use the includes directory to hold cached copies of remote includes, some of which may have
been modified. 
* Also both approaches would need the XML-comment workaround to retain the history of untitled external
XInclude directives that are modified to reference cached copies. 
* Both approaches allow duplicate (but non-circular) titled includes to use the same cached copy of a remote file. 

The directory /input holds the original corpus, with importations.
The directories /result1 and /result2 holds a logically-equivalent corpus where all importations that are resolvable have been rewritten
in terms of XInclude statements.

We assume that all files at the top level (directly under /input or /resultx) constitute the corpus.
