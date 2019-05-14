# Yummy text

#### The ASCII text glutton 

A simple REST API to digest text files:

#### `Get /status` 

Fetch service health status

*Response* 200 Ok

#### `Post /digest`
Expects a multiPart request with part example: Part("file" -> "File is a text file.")

*Response*

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

*Error response*

`400 BadRequest`

Body will show error message.

### Run tests

`sbt test`

### Run service

`sbt run`
