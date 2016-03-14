List Courses for Profile
========================

This API will return list of courses based on the subject passed in request. If current logged in user id matches to the user id in URI, it will list courses for owner.
If logged in user id do NOT match with user id in URI, it will list courses for public (user viewing profile of other user).

Method
------

GET

End Point
---------

http://{host}/api/nucleus/{version}/profiles/{user-id}/courses?subject={subject-bucket}&limit={limit}&offset={offset}

Sample: http://nucleus.gooru.org/api/nucleus/v1/profiles/852f9814-0eb4-461d-bd3b-aca9c2500595/courses?subject=K12 Science

Parameters
----------

| Parameter Name  | Type | Required  | Default | Description  |
|-----------------|------|-----------|---------|--------------|
| subject | String  |   |   | Subject bucket to filter the courses. E.g. "K12 Math" or "K12 Science"|


Headers
-------

Request Body
------------