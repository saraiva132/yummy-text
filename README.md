# Yummy text

#### The ASCII text glutton 

### Run tests

`sbt test`

### Run service

`sbt run`

### REST API

A simple REST API with status endpoint and endpoint that digest files

#### `Get /status` 

Fetch service health status

*Response* 200 Ok

#### `Post /digest`

Expects a HTTP multi-part request with text file to be uploaded in part with part name file

The maximum file size is 10 MBs

Example: 

Header("file" -> "File is a text file.")

*Response* 200 Ok

```json
{
  "wordCount" : 5,
  "wordOccurrences" : {
    "file" : 2,
    "is"   : 1,
    "a"    : 1,
    "text" : 1
  }
}
```

Error response

`400 BadRequest` Body will show string with error message.

`500 InternalServerError` Something really unexpected happened.


