SELECT current_database();

-- ✅ Seed: products
INSERT INTO products (id, name, price_in_cents)
VALUES
  (1, 'Flour', 250),
  (2, 'Eggs', 120),
  (3, 'Sugar', 180),
  (4, 'Milk', 150),
  (5, 'Butter', 300);

-- ✅ Seed: recipes
INSERT INTO recipes (id, name)
VALUES
  (1, 'Pancakes'),
  (2, 'Cake');

-- ✅ Seed: recipe_ingredients
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity)
VALUES
  (1, 1, 2),  -- Flour in Pancakes
  (1, 2, 3),  -- Eggs in Pancakes
  (1, 4, 1),  -- Milk in Pancakes

  (2, 1, 3),  -- Flour in Cake
  (2, 2, 2),  -- Eggs in Cake
  (2, 3, 2),  -- Sugar in Cake
  (2, 5, 1);  -- Butter in Cake
