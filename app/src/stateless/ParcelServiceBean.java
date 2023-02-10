package stateless;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.Parcel;

@Stateless
public  class ParcelServiceBean {

  /*
   * Instance variables
   */
  @PersistenceContext(unitName="swcar")
  private EntityManager entityManager;

  public void setEntityManager(EntityManager localEntityManager){
    entityManager = localEntityManager;
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public Parcel create(Parcel newParcel) {
    getEntityManager().persist(newParcel);
    return newParcel;
  }

  /**
   * Elimina un campo mediante su id
   *
   * @param  id
   * @return No nulo en caso de haber eliminado el campo, en caso contrario nulo
   */
  public Parcel remove(int id) {
    Parcel parcel = find(id);

    if (parcel != null) {
      parcel.setActive(false);
      return parcel;
    }

    return null;
  }

  /**
   * Actualiza o modifica la entidad asociada al id dado
   *
   * @param  id
   * @param  modifiedParcel
   * @return un valor no nulo en caso de modificar la entidad solicitada
   * mediante el id, en caso contrario retorna un valor nulo
   */
  public Parcel modify(int id, Parcel modifiedParcel) {
    Parcel givenParcel = find(id);

    if (givenParcel != null) {
      /*
       * TODO: Leer
       * Probablemente se tenga que hacer una validacion
       * que impida que un mismo usuario cliente cargue
       * para si mismo mas de una parcela con nombre
       * repetido
       */
      givenParcel.setName(modifiedParcel.getName());
      givenParcel.setHectare(modifiedParcel.getHectare());
      givenParcel.setLongitude(modifiedParcel.getLongitude());
      givenParcel.setLatitude(modifiedParcel.getLatitude());
      givenParcel.setActive(modifiedParcel.getActive());
      return givenParcel;
    }

    return null;
  }

  public Parcel find(int id) {
    return getEntityManager().find(Parcel.class, id);
  }

  public Collection<Parcel> findAll() {
    Query query = getEntityManager().createQuery("SELECT p FROM Parcel p ORDER BY p.id");
    return (Collection) query.getResultList();
  }

  /**
   * @return coleccion con todas las parcelas que estan activas
   */
  public Collection<Parcel> findAllActive() {
    Query query = getEntityManager().createQuery("SELECT p FROM Parcel p WHERE p.active = TRUE ORDER BY p.id");
    return (Collection) query.getResultList();
  }

  public Page<Parcel> findByPage(Integer page, Integer cantPerPage, Map<String, String> parameters) {
    // Genero el WHERE dinámicamente
    StringBuffer where = new StringBuffer(" WHERE 1=1");

    if (parameters != null)
    for (String param : parameters.keySet()) {
      Method method;
      try {
        method = Parcel.class.getMethod("get" + capitalize(param));
        if (method == null || parameters.get(param) == null || parameters.get(param).isEmpty()) {
          continue;
        }
        switch (method.getReturnType().getSimpleName()) {
          case "String":
          where.append(" AND UPPER(e.");
          where.append(param);
          where.append(") LIKE UPPER('%");
          where.append(parameters.get(param));
          where.append("%')");
          break;
          default:
          where.append(" AND e.");
          where.append(param);
          where.append(" = ");
          where.append(parameters.get(param));
          break;
        }
      } catch (NoSuchMethodException | SecurityException e) {
        e.printStackTrace();
      }
    }

    // Cuento el total de resultados
    Query countQuery = getEntityManager()
        .createQuery("SELECT COUNT(e.id) FROM " + Parcel.class.getSimpleName() + " e" + where.toString());

    // Pagino
    Query query = getEntityManager().createQuery("FROM " + Parcel.class.getSimpleName() + " e" + where.toString());
    query.setMaxResults(cantPerPage);
    query.setFirstResult((page - 1) * cantPerPage);
    Integer count = ((Long) countQuery.getSingleResult()).intValue();
    Integer lastPage = (int) Math.ceil((double) count / (double) cantPerPage);

    // Armo respuesta
    Page<Parcel> resultPage = new Page<Parcel>(page, count, page > 1 ? page - 1 : page,
        page > lastPage ? page + 1 : lastPage, lastPage, query.getResultList());
    return resultPage;
  }

  private String capitalize(final String line) {
    return Character.toUpperCase(line.charAt(0)) + line.substring(1);
  }

}
