package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.BaseRepository;
import ch.puzzle.jee.userauth.security.entity.Permission;
import ch.puzzle.jee.userauth.security.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityGraph;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@ApplicationScoped
public class PermissionRepository extends BaseRepository<Permission> {

    public List<User> findAllUsersWithPermissions() {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        CriteriaQuery<User> all = query.select(query.from(User.class)).distinct(true);

        EntityGraph<User> entityGraph = getEntityManager().createEntityGraph(User.class);
        entityGraph.addSubgraph("roles").addSubgraph("permissions");

        return getEntityManager().createQuery(all).setHint("javax.persistence.loadgraph", entityGraph).getResultList();
    }
}