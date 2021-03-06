package ua.restaurant.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.restaurant.vote.model.Dish;

import java.util.List;

/**
 * Created by Galushkin Pavel on 14.03.2017.
 */
@Transactional(readOnly = true)
public interface DishRepository extends JpaRepository<Dish, Integer> {
    @Override
    Dish save(Dish menu);

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish m WHERE m.id=:id AND m.restaurant.id=:restaurantId")
    int delete(@Param("id") int id, @Param("restaurantId") int restaurantId);

    @Override
    Dish findOne(Integer id);

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=?1 ORDER BY d.date DESC")
    List<Dish> getAllByRestaurant(int restaurantId);
}