List Courses for Profile
========================

This API will return list of courses based on the subject passed in request. If current logged in user id matches to the user id in URI, it will list courses for owner.
If logged in user id do NOT match with user id in URI, it will list courses for public (user viewing profile of other user).

###Method

GET

###End Point

http://{host}/api/nucleus/{version}/profiles/{user-id}/courses?subject={subject-bucket}&limit={limit}&offset={offset}

Sample: 
http://nucleus.gooru.org/api/nucleus/v1/profiles/852f9814-0eb4-461d-bd3b-aca9c2500595/courses?subject=K12 Science

###Parameters

|  Name  | Type | Required  | Default | Description  |
|--------|------|-----------|---------|--------------|
| subject | String  |   |   | Subject bucket to filter the courses. E.g. "K12 Math" or "K12 Science"|
| limit | int | | 20  | Limit number of records to be displayed on the page |
| offset  | int | | 0 | Start index of record to be displayed on the page |


###Headers

| Name  | Value |
|-------|-------|
| Authorization | Token [TOKEN] |

* Replace [TOKEN] with actual value of access token obtained from authentication call.

###Response Body

```json
{
  "courses": [
    {
      "id": "b9e232a8-1738-42d2-8abf-8ad14610acec",
      "title": "mathematics course 101",
      "publish_status": "unpublished",
      "thumbnail": "http://thumbnails-demo.s3.amazonaws.com/74266efb-74eb-45de-a6a8-4052710af82c.png",
      "owner_id": "e638c5b7-183d-4838-98ab-aa7783be9eaa",
      "original_creator_id": null,
      "collaborator": [
        "852f9814-0eb4-461d-bd3b-aca9c2500595"
      ],
      "taxonomy": [
        "K12.MA-MA2"
      ],
      "sequence_id": 1,
      "visible_on_profile": true,
      "unit_count": 1,
      "owner_info": {
        "id": "e638c5b7-183d-4838-98ab-aa7783be9eaa",
        "firstname": "User",
        "lastname": "One",
        "thumbnail_path": "http://thumbnails-demo.s3.amazonaws.com/74266efb-74eb-45de-a6a8-4052710af82c.png"
      }
    },
    {
      "id": "1c2d0f0e-ddb0-4b9b-a3e7-e57ff475046e",
      "title": "mathematics course 102",
      "publish_status": "published",
      "thumbnail": "http://thumbnails-demo.s3.amazonaws.com/74266efb-74eb-45de-a6a8-4052710af82c.png",
      "owner_id": "852f9814-0eb4-461d-bd3b-aca9c2500595",
      "original_creator_id": null,
      "collaborator": [
        "c59fa706-d5f5-4230-a241-1eb96b3ff0e1",
        "8dc3322f-4b59-4102-98e8-43e0fcc346fe",
        "3d6bc3ea-2aff-4106-8762-9246d5d84813"
      ],
      "taxonomy": [
        "K12.MA-MA2"
      ],
      "sequence_id": 2,
      "visible_on_profile": true,
      "unit_count": null,
      "owner_info": {
        "id": "852f9814-0eb4-461d-bd3b-aca9c2500595",
        "firstname": "Sachin",
        "lastname": "Zope",
        "thumbnail_path": null
      }
    },
    {
      "id": "e5e9716f-35d1-46ff-b68b-274699d60085",
      "title": "Course 111",
      "publish_status": "unpublished",
      "thumbnail": "http://thumbnails-demo.s3.amazonaws.com/74266efb-74eb-45de-a6a8-4052710af82c.png",
      "owner_id": "852f9814-0eb4-461d-bd3b-aca9c2500595",
      "original_creator_id": null,
      "collaborator": [
        "c59fa706-d5f5-4230-a241-1eb96b3ff0e1",
        "8dc3322f-4b59-4102-98e8-43e0fcc346fe",
        "3d6bc3ea-2aff-4106-8762-9246d5d84813"
      ],
      "taxonomy": [
        "K12.MA-MA2"
      ],
      "sequence_id": 3,
      "visible_on_profile": true,
      "unit_count": null,
      "owner_info": {
        "id": "852f9814-0eb4-461d-bd3b-aca9c2500595",
        "firstname": "Sachin",
        "lastname": "Zope",
        "thumbnail_path": null
      }
    }
  ],
  "filters": {
    "subject": "K12 Science",
    "limit": 20,
    "offset": 0
  }
}
```

###Response Codes

Refer to [document](https://docs.google.com/document/d/1GCYBjZsc-_lyNcUW3yEKfjQtIMSFggWc0n9Vwgsi85Y/edit#heading=h.eb0v4yfpkh96) for HTTP Response Codes.
