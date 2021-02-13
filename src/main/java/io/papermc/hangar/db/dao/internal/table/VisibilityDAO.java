package io.papermc.hangar.db.dao.internal.table;

import io.papermc.hangar.model.db.visibility.ProjectVersionVisibilityChangeTable;
import io.papermc.hangar.model.db.visibility.ProjectVisibilityChangeTable;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.stereotype.Repository;

@Repository
@RegisterConstructorMapper(ProjectVisibilityChangeTable.class)
@RegisterConstructorMapper(ProjectVersionVisibilityChangeTable.class)
public interface VisibilityDAO {

    // Projects
    @Timestamped
    @SqlUpdate("INSERT INTO project_visibility_changes (created_at, created_by, project_id, comment, resolved_at, resolved_by, visibility) " +
            "VALUES (:now, :createdBy, :projectId, :comment, :resolvedAt, :resolvedBy, :visibility)")
    void insert(@BindBean ProjectVisibilityChangeTable projectVisibilityChangeTable);

    @Timestamped
    @SqlUpdate("UPDATE project_visibility_changes pvc " +
            "SET resolved_at = :now, resolved_by = :userId " +
            "FROM " +
            "(SELECT project_id FROM project_visibility_changes WHERE project_id = :projectId AND resolved_at IS NULL AND resolved_by IS NULL ORDER BY created_at LIMIT 1) AS sq " +
            "WHERE pvc.project_id = sq.project_id")
    void updateLatestProjectChange(long userId, long projectId);

    // Versions
    @Timestamped
    @SqlUpdate("INSERT INTO project_version_visibility_changes (created_at, created_by, version_id, comment, resolved_at, resolved_by, visibility) " +
            "VALUES (:now, :createdBy, :versionId, :comment, :resolvedAt, :resolvedBy, :visibility)")
    void insert(@BindBean ProjectVersionVisibilityChangeTable projectVersionVisibilityChangesTable);

    @Timestamped
    @SqlUpdate("UPDATE project_version_visibility_changes " +
            "SET resolved_at = :now, resolved_by = :userId " +
            "FROM" +
            "    (SELECT version_id FROM project_version_visibility_changes WHERE version_id = :versionId AND resolved_at IS NULL AND resolved_by IS NULL ORDER BY created_at LIMIT 1) as subquery " +
            "WHERE project_version_visibility_changes.version_id = subquery.version_id")
    void updateLatestVersionChange(long userId, long versionId);
}