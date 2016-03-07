Nucleus Profile
================

This is the Profile handler for Project Nucleus. 

This project contains just one main verticle which is responsible for listening for profile address on message bus. 

DONE
----
* Instead of publish date, send publish status of the content.

TODO
----
* Identify the taxonomy tag classification (K12, Higher Education etc.) and update list of levels for FetchTaxonomy APIs
* For all APIs - Taxonomy transformation
* If collaborator information in not getting displayed on cards of profile pages then just send boolean flag denoting collaborator presence for content
* Currently sorting on title is insensitive. Title starting with capital letters will arrange first and then starting with small letters 
* Verify required sorting on course - courses should be listed based on sequence_id. No need to have sortOn and order parameters
 

To understand build related stuff, take a look at **BUILD_README.md**.


