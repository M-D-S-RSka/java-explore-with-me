package ru.practicum.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.db.model.DbHitData;

public interface StatsRepo extends JpaRepository<DbHitData, Long>, JpaSpecificationExecutor<DbHitData> {

    String getAppFromDatabase();
}
