# API Reference

## Base URL
```
http://localhost:8081/api
```

## Endpoints

### Create Product
**POST** `/products`

Creates a new product.

**Request Body**:
```json
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "inStock": true
}
```

**Validation Rules**:
- `name`: Required, 2-100 characters
- `description`: Optional, max 1000 characters
- `price`: Required, minimum 0.01
- `stockQuantity`: Optional
- `inStock`: Optional

**Response**: `201 Created`
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "inStock": true
}
```

---

### Get Product by ID
**GET** `/products/{id}`

Retrieves a single product by ID.

**Response**: `200 OK`
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "inStock": true
}
```

**Error**: `404 Not Found`
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 1",
  "path": "/api/products/1"
}
```

---

### Get All Products
**GET** `/products?page=0&size=20&sort=name,asc`

Retrieves paginated list of products.

**Query Parameters**:
- `page`: Page number (default: 0)
- `size`: Items per page (default: 20)
- `sort`: Sort field and direction (e.g., `price,desc`)

**Response**: `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "name": "Laptop",
      "description": "High-performance laptop",
      "price": 999.99,
      "stockQuantity": 50,
      "inStock": true
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 1,
  "empty": false
}
```

---

### Update Product
**PUT** `/products/{id}`

Updates an existing product.

**Request Body**:
```json
{
  "name": "Gaming Laptop",
  "description": "Updated description",
  "price": 1299.99,
  "stockQuantity": 30,
  "inStock": true
}
```

**Response**: `200 OK`
```json
{
  "id": 1,
  "name": "Gaming Laptop",
  "description": "Updated description",
  "price": 1299.99,
  "stockQuantity": 30,
  "inStock": true
}
```

**Error**: `404 Not Found` if product doesn't exist

---

### Delete Product
**DELETE** `/products/{id}`

Deletes a product by ID.

**Response**: `204 No Content`

**Error**: `404 Not Found` if product doesn't exist

---

## Error Responses

### Validation Error
**Status**: `400 Bad Request`
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "errors": [
    {
      "field": "name",
      "message": "Product name is required"
    },
    {
      "field": "price",
      "message": "Price must be greater than 0.00"
    }
  ]
}
```

### Not Found Error
**Status**: `404 Not Found`
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/products/999"
}
```

---

## Health Check
**GET** `/actuator/health`

**Response**: `200 OK`
```json
{
  "status": "UP"
}
```
