
## Queries
You can run queries interactively using [GraphiQL](https://github.com/graphql/graphiql) by opening http://localhost:8080 in a browser or query the `/graphql` endpoint. It accepts following properties in the JSON body (this follows [relay](https://facebook.github.io/relay) convention):

* `query` - String - GraphQL query as a string
* `variables` - String - containing JSON object that defines variables for your query _(optional)_
* `operationName` - String - the name of the operation, in case you defined several of them in the query _(optional)_

Here are some examples of the queries you can make:

#### Object model with filtered object attributes and timeseries (by keys)

```graphql
query($namespace: String!, $oaKeys: [ID!], $tsKeys: [ID!]) {
  model(namespace: $namespace) {
    namespace
    timeseries(keys: $tsKeys) {
      key
      displayName
    }
    objectAttributes(keys: $oaKeys) {
      key,
      description
    }
  }
}
```

#### Object model without filtering

```graphql
query($namespace: String!) {
  model(namespace: $namespace) {
    namespace
    timeseries {
      key
      displayName
    }
    objectAttributes {
      key,
      description
    }
  }
}

```