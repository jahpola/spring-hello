# Product API Documentation

This document provides details about the Product API endpoints available in the Spring Hello application.

Base URL: `/api/products`

## Endpoints

### Get All Products

Retrieves a list of all products.

- **URL**: `/api/products`
- **Method**: `GET`
- **Auth required**: No
- **Permissions required**: None

#### Success Response

- **Code**: `200 OK`
- **Content example**:

```json
[
  {
    "id": 1,
    "name": "Product Name",
    "description": "Product description",
    "price": 19.99,
    "stockQuantity": 100,
    "inStock": true
  },
  {
    "id": 2,
    "name": "Another Product",
    "description": "Another description",
    "price": 29.99,
    "stockQuantity": 50,
    "inStock": true
  }
]
```

### Get Product by ID

Retrieves a specific product by its ID.

- **URL**: `/api/products/{id}`
- **Method**: `GET`
- **URL Parameters**: `id=[Long]` where `id` is the ID of the product
- **Auth required**: No
- **Permissions required**: None

#### Success Response

- **Code**: `200 OK`
- **Content example**:

```json
{
  "id": 1,
  "name": "Product Name",
  "description": "Product description",
  "price": 19.99,
  "stockQuantity": 100,
  "inStock": true
}
```

#### Error Response

- **Code**: `404 NOT FOUND`
- **Content**: None

### Create Product

Creates a new product.

- **URL**: `/api/products`
- **Method**: `POST`
- **Auth required**: No
- **Permissions required**: None
- **Request body**: Product object

```json
{
  "name": "New Product",
  "description": "Product description",
  "price": 19.99,
  "stockQuantity": 100,
  "inStock": true
}
```

#### Success Response

- **Code**: `201 CREATED`
- **Content example**:

```json
{
  "id": 3,
  "name": "New Product",
  "description": "Product description",
  "price": 19.99,
  "stockQuantity": 100,
  "inStock": true
}
```

### Update Product

Updates an existing product.

- **URL**: `/api/products/{id}`
- **Method**: `PUT`
- **URL Parameters**: `id=[Long]` where `id` is the ID of the product to update
- **Auth required**: No
- **Permissions required**: None
- **Request body**: Product object with updated fields

```json
{
  "name": "Updated Product Name",
  "description": "Updated description",
  "price": 24.99,
  "stockQuantity": 75,
  "inStock": true
}
```

#### Success Response

- **Code**: `200 OK`
- **Content example**:

```json
{
  "id": 1,
  "name": "Updated Product Name",
  "description": "Updated description",
  "price": 24.99,
  "stockQuantity": 75,
  "inStock": true
}
```

#### Error Response

- **Code**: `404 NOT FOUND`
- **Content**: None

### Delete Product

Deletes a product.

- **URL**: `/api/products/{id}`
- **Method**: `DELETE`
- **URL Parameters**: `id=[Long]` where `id` is the ID of the product to delete
- **Auth required**: No
- **Permissions required**: None

#### Success Response

- **Code**: `204 NO CONTENT`
- **Content**: None

## Data Model

### Product

| Field         | Type        | Description                                   | Constraints                |
|---------------|-------------|-----------------------------------------------|----------------------------|
| id            | Long        | Unique identifier for the product             | Auto-generated             |
| name          | String      | Name of the product                           | Required                   |
| description   | String      | Description of the product                    | Max length: 1000 characters|
| price         | BigDecimal  | Price of the product                          | Required, precision: 10, scale: 2 |
| stockQuantity | Integer     | Available quantity in stock                   | Optional                   |
| inStock       | Boolean     | Indicates if the product is currently in stock| Optional                   |

## Notes

- The API uses standard HTTP status codes to indicate success or failure
- All endpoints return JSON responses
- The API includes observability through the `@Observed` annotation on create, update, and delete operations