package edu.gatech.cs6310.assignment5.repository;

import edu.gatech.cs6310.assignment5.model.Item;
import edu.gatech.cs6310.assignment5.model.ItemId;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, ItemId> {
}
