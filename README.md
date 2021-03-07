# aggregator
API Services Aggregator

## How to:

#### Required settings:
***For dev and test to work is required to get a local mongodb environment running under*** [Dev Config](https://github.com/Flor-de-Frida/aggregator/blob/main/src/main/resources/application-dev.yml)

#### Test:
```bash
mvn clean test
```

#### Run Dev:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Run Prod:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Docker run
```bash
docker build -t flor-de-frida/aggregator .
docker run -p 8080:8080 -it --rm --name flor-de-frida-aggregator flor-de-frida/aggregator
```

## Endpoints:

- Products:
  - Create New Product:
    - Request: `POST /products` --headers = 'x-organization'
        - Request Blueprint:
          ```json
          {
              "sku": "F12345",
              "name": "Product Name",
              "slugName": "product-slug-name",
              "gtin": "GTIN-F12345",
              "organizationSlugName": "product-organization-slug-name",
              "brandSlugName": "product-brand",
              "description": "Product Description",
              "availableOnDemand": true,
              "available": true
          }
          ```
        - Success Response Blueprint:
          ```json5
          {
              "status": 201,
              "message": "product-created-success",
              "result": {
                  "sku": "F12345",
                  "name": "Product Name",
                  "slugName": "product_slug_name",
                  "gtin": "GTIN-F12345",
                  "organizationSlugName": "product_organization_slug_name",
                  "updateInfo": {
                      "moment":"2020-11-16T21:13:19.138919", /* UTC format */
                      "author": "system@flordefrida.com.br"
                  },
                  "creationInfo": {
                      "moment":"2020-11-16T21:13:19.139023", /* UTC format */
                      "author": "system@flordefrida.com.br"
                  },
                  "brandSlugName": "product-brand",
                  "description": "Product Description",
                  "availableOnDemand": true,
                  "available": true
              }
          }
          ```
        - Conflict Response Blueprint:
          ```json
          {
              "status": 409,
              "message": "Product with sku=F12345 already exists",
              "reason": "product-already-exists-for-sku",
              "reasonId": "F12345"
          }
          ```
        - Invalid Request Blueprint:
          ```json
          {
              "status": 400,
              "message": "Product with errors in fields invalid-empty-sku=\\$.sku",
              "reason": "invalid-product",
              "reasonId": "\\$.sku"
          }
          ```

  - Delete Product By SKU: (***also deletes it's images from cloudinary***)
    - Request: `DELETE /products/sku/{sku}`  --headers = 'x-organization'
        - Success Response Blueprint:
          ```json5
          {
              "status": 200,
              "message": "product-updated-success",
              "result": {
                  "sku": "F12345",
                  "name": "Product Name",
                  "slugName": "product-slug-name",
                  "gtin": "GTIN-F12345",
                  "organizationSlugName": "product-organization-slug-name",
                  "updateInfo": {
                      "moment":"2020-11-16T21:13:19.138919", /* UTC format */
                      "author": "system@flordefrida.com.br"
                  },
                  "creationInfo": {
                      "moment":"2020-11-16T21:13:19.139023", /* UTC format */
                      "author": "system@flordefrida.com.br"
                  },
                  "brandSlugName": "product-brand",
                  "description": "Product Description",
                  "availableOnDemand": true,
                  "available": true,
                  "images": [
                    "https://`<secured-cloudinary-url>`/`<organization-name>`/`<sku>`/`<image1-name>.jpg`",
                    "https://`<secured-cloudinary-url>`/`<organization-name>`/`<sku>`/`<image2-name>.jpg`"
                  ]
              }
          }
          ```
        - Error Response Blueprint:
          ```json
          {
              "status": 404,
              "message": "Product with sku=unknown,organizationSlugName=flor-de-frida not found",
              "reason": "product-not-found",
              "reasonId": "unknown,flor-de-frida"
          }
          ```

  - Find Product:
      - Request: `GET /products/sku/{sku}`  --headers = 'x-organization'
          - Response Blueprint:
              ```json5
              {
                  "status": 200,
                  "message": "product-found-success",
                  "result": {
                      "sku": "F12345",
                      "name": "Product Name",
                      "slugName": "product-slug-name",
                      "gtin": "GTIN-F12345",
                      "organizationSlugName": "product-organization-slug-name",
                      ...
                  }
              }
              ```
          - Failure Response Blueprint:
          ```json
              {
                  "status": 404,
                  "message": "Product with sku=unknown,organizationSlugName=flor-de-frida not found",
                  "reason": "product-not-found",
                  "reasonId": "unknown,flor-de-frida"
              }
          ```
      - Request: `GET /products/{slugName}`
          - Response Blueprint:
              ```json5
              {
                  "status": 200,
                  "message": "product-found-success",
                  "result": {
                      "sku": "F12345",
                      "name": "Product Name",
                      "slugName": "product-slug-name",
                      "gtin": "GTIN-F12345",
                      "organizationSlugName": "product-organization-slug-name",
                      ...
                  }
              }
              ```
          - Failure Response Blueprint:
          ```json
              {
                  "status": 404,
                  "message": "Product with slugName=unknown,organizationSlugName=flor-de-frida not found",
                  "reason": "product-not-found",
                  "reasonId": "unknown,flor-de-frida"
              }
          ```
      - Request: `GET /products/?page=1&size=10&fl=*`  --headers = 'x-organization'
          - Response Blueprint:
              ```json5
              {
                  "status": 200,
                  "message": "products-found-success",
                  "results": [{
                      "sku": "F12345",
                      "name": "Product Name",
                      "slugName": "product-slug-name",
                      "gtin": "GTIN-F12345",
                      "organizationSlugName": "product-organization-slug-name",
                      ...
                  }],
                  "page": 1,
                  "size": 10,
                  "total": 1
              }
              ```
      - Request: `GET /products/?page=1&size=10&fl=name`  --headers = 'x-organization'
          - Response Blueprint:
              ```json5
              {
                  "status": 200,
                  "message": "products-found-success",
                  "results": [{
                      "name": "Product Name",
                  }],
                  "page": 1,
                  "size": 10,
                  "total": 1
              }
              ```
  - Upload Product Image:
      - Request: `PUT /products/images/{sku}`  --headers = 'x-organization' --form-data image=(Image File)
          - Response Blueprint:
              ```json5
              {
                  "status": 200,
                  "message": "product-image-uploaded",
                  "result": {
                      "sku": "F12345",
                      "name": "Product Name",
                      "slugName": "product-slug-name",
                      "gtin": "GTIN-F12345",
                      "organizationSlugName": "product-organization-slug-name",
                      ...,
                      "images": [
                        "https://`<secured-cloudinary-url>`/`<organization-name>`/`<sku>`/`<image1-name>.jpg`",
                        "https://`<secured-cloudinary-url>`/`<organization-name>`/`<sku>`/`<image2-name>.jpg`"
                      ]
                  }
              }
              ```
          - Failure Response Blueprint:
          ```json
              {
                  "status": 404,
                  "message": "Product with sku=unknown,organizationSlugName=flor-de-frida not found",
                  "reason": "product-not-found",
                  "reasonId": "unknown,flor-de-frida"
              }
          ```
      - Request: `DELETE /products/images/{sku}/image/{imageName}`
          - Response Blueprint:
              ```json5
              {
                  "status": 200,
                  "message": "product-image-deleted",
                  "result": {
                      "sku": "F12345",
                      "name": "Product Name",
                      "slugName": "product-slug-name",
                      "gtin": "GTIN-F12345",
                      "organizationSlugName": "product-organization-slug-name",
                      ...,
                      "images": [
                        "https://`<secured-cloudinary-url>`/`<organization-name>`/`<sku>`/`<image2-name>.jpg`"
                      ]
                  }
              }
              ```
          - Failure Response Blueprint:
          ```json
              {
                  "status": 404,
                  "message": "Product with slugName=unknown,organizationSlugName=flor-de-frida not found",
                  "reason": "product-not-found",
                  "reasonId": "unknown,flor-de-frida"
              }
          ```
