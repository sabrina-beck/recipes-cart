# Decision-making process
## Idempotency
This API is designed to be idempotent where applicable, ensuring safe and predictable behavior even in the case of 
repeated or retried requests.

### ➕ `POST /carts/{cartId}/add_recipe`
This endpoint is idempotent.

- If a recipe is already present in the cart, its quantity will be replaced by the value provided in the request.
- If the recipe is not in the cart yet, it will be added with the specified quantity.
- Sending the same request multiple times produces the same result, ensuring safe retries and predictable outcomes.

This behavior follows a common pattern in e-commerce systems where updating a cart item reflects the latest desired quantity from the user.

### ➖ `DELETE /carts/{cartId}/recipes/{recipeId}`
This endpoint is also idempotent.
- If the recipe is present in the cart, it will be removed.
- If the recipe is not in the cart, the operation is still considered successful and does not produce an error.
- This avoids race conditions and makes the operation safe to retry.