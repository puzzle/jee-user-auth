package ch.puzzle.jee.userauth;

import ch.puzzle.jee.userauth.auditing.entity.AuditedEntity;
import ch.puzzle.jee.userauth.mappers.boundary.ErrorMapper;
import ch.puzzle.jee.userauth.security.boundary.PermissionInterceptor;
import ch.puzzle.jee.userauth.security.boundary.RequiresPermissionAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.Interceptors;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.puzzle.jee.userauth.mappers.boundary.ErrorMapper.KEY_ENTITY_NOT_FOUND;
import static ch.puzzle.jee.userauth.security.entity.Action.*;
import static javax.ws.rs.core.Response.Status.*;

/**
 * Abstract base class that provides support for basic CRUD operations on entities.
 *
 * @param <T> The Entity Type.
 */
@Interceptors(PermissionInterceptor.class)
public abstract class EntityResource<T extends AuditedEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(EntityResource.class);

    private final Class<T> entityType;

    protected abstract BaseRepository<T> getRepository();

    protected EntityResource() {
        entityType = Objects.requireNonNull(ReflectionUtil.<T>getActualTypeArguments(getClass(), 0));
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    @POST
    @RequiresPermissionAction(CREATE)
    public Response persist(@Context UriInfo uriInfo, @Valid T entity) {
        LOG.debug("Persisting a new entity: {}", entity);
        doPersist(entity);
        URI path = uriInfo.getAbsolutePathBuilder().path("/" + entity.getId()).build();
        return Response.created(path).entity(entity).build();
    }

    /**
     * Can be overridden in a subclass to change the default persist behaviour.
     */
    protected void doPersist(@Valid T entity) {
        getRepository().persist(entity);
    }

    @PUT
    @Path("/{id}")
    @RequiresPermissionAction(UPDATE)
    public Response merge(@PathParam("id") Long id, @Valid T entity) {
        LOG.debug("Updating existing entity: {}", entity);

        Objects.requireNonNull(entity.getVersion(), entityType.getSimpleName() + ".version must not be null");
        Objects.requireNonNull(entity.getId(), entityType.getSimpleName() + ".id must not be null");

        T mergedEntity = getRepository().merge(entity);
        return ok(mergedEntity);
    }

    @DELETE
    @Path("/{id}")
    @RequiresPermissionAction(DELETE)
    public Response remove(@PathParam("id") Long id) {
        LOG.debug("Deleting {} with id {}", entityType.getSimpleName(), id);

        boolean entityFound = getRepository().remove(id);
        return entityFound ? Response.status(NO_CONTENT).build() : Response.status(NOT_FOUND).build();
    }

    @GET
    @RequiresPermissionAction(READ)
    public Response findAll() {
        LOG.debug("Finding all {}s", entityType.getSimpleName());
        List<T> entities = doFindAll();
        return ok(entities != null ? entities : new ArrayList<>());
    }

    /**
     * Can be overridden in a subclass to change the default loading strategy.
     *
     * @return A List of entities T.
     */
    protected List<T> doFindAll() {
        return getRepository().findAll();
    }

    @GET
    @Path("/{id}")
    @RequiresPermissionAction(READ)
    public Response findById(@PathParam("id") Long id) {
        LOG.debug("Finding {} with id {}", entityType.getSimpleName(), id);
        AuditedEntity entity = doFindById(id);
        if (entity == null) {
            return entityNotFound();
        }
        return ok(entity);
    }

    /**
     * Can be overridden in a subclass to change the default loading strategy.
     *
     * @return the entity for the provided id. Null if the entity was not found.
     */
    protected T doFindById(Long id) {
        return getRepository().find(id);
    }

    @OPTIONS
    public Response options() {
        return ok();
    }

    public Response ok() {
        return Response.ok().build();
    }

    public Response ok(Object entity) {
        return Response.ok(entity).build();
    }

    public Response entityNotFound() {
        JsonObject error = ErrorMapper.create().addMessageKey(KEY_ENTITY_NOT_FOUND).build();
        return Response.status(NOT_FOUND).entity(error).build();
    }

    public Response businessError(String key) {
        JsonObject error = ErrorMapper.create().addMessageKey(key).build();
        return Response.status(BAD_REQUEST).entity(error).build();
    }
}
