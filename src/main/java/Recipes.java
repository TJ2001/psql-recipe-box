import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Recipes implements Getter {
  private String name;
  private String ingredients;
  private String instructions;
  private int rating;
  private int id;


  public Recipes(String name, String ingredients, String instructions) {
    this.name = name;
    this.ingredients = ingredients;
    this.instructions = instructions;
  }

  public String getName() {
    return name;
  }

  public String getIngredients() {
    return ingredients;
  }

  public String getInstructions() {
    return instructions;
  }

  public static List<Recipes> all() {
    String sql = "SELECT * FROM recipes";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Recipes.class);
    }
  }

  public int getId() {
    return id;
  }

 public static Recipes find(int id) {
     try(Connection con = DB.sql2o.open()) {
       String sql = "SELECT * FROM recipes where id=:id";
       Recipes recipe = con.createQuery(sql)
         .addParameter("id", id)
         .executeAndFetchFirst(Recipes.class);
       return recipe;
     }
   }



 @Override
 public boolean equals(Object otherRecipes) {
   if (!(otherRecipes instanceof Recipes)) {
     return false;
   } else {
     Recipes newRecipes = (Recipes) otherRecipes;
     return this.getName().equals(newRecipes.getName()) &&
            this.getIngredients().equals(newRecipes.getIngredients()) &&
            this.getInstructions().equals(newRecipes.getInstructions());

   }
 }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO recipes(name, ingredients, instructions) VALUES (:name, :ingredients, :instructions)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .addParameter("ingredients", this.ingredients)
        .addParameter("instructions", this.instructions)
        .executeUpdate()
        .getKey();
    }
  }

  public List<Category> getCategories() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT categories_id FROM recipes_tags WHERE recipes_id = :recipes_id";
      List<Integer> categoryIds = con.createQuery(joinQuery)
        .addParameter("recipes_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Category> categories = new ArrayList<Category>();

      for (Integer categoryId : categoryIds) {
        String categoryQuery = "SELECT * FROM categories WHERE id = :categoryId";
        Category category = con.createQuery(categoryQuery)
          .addParameter("categoryId", categoryId)
          .executeAndFetchFirst(Category.class);
        categories.add(category);
      }
      return categories;
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM recipes WHERE id=:id;";
      con.createQuery(sql)
      .addParameter("id", id)
      .executeUpdate();

      sql = "DELETE FROM recipes_tags WHERE recipes_id = :recipes_id";
      con.createQuery(sql)
      .addParameter("recipes_id", this.id)
      .executeUpdate();
    }
  }

  public void update(String name, String ingredients, String instructions) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE recipes SET name = :name, ingredients =:ingredients, instructions = :instructions WHERE id=:id";
      con.createQuery(sql)
      .addParameter("name", name)
      .addParameter("ingredients", ingredients)
      .addParameter("instructions", instructions)
      .addParameter("id", this.id)
      .executeUpdate();

      sql = "DELETE FROM recipes_tags WHERE recipes_id = :recipes_id";
      con.createQuery(sql)
      .addParameter("recipes_id", this.id)
      .executeUpdate();
    }
  }
}
