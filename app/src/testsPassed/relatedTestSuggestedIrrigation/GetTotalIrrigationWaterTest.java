import static org.junit.Assert.*;

import java.util.Calendar;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import model.Parcel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import stateless.IrrigationRecordServiceBean;
import stateless.ParcelServiceBean;

public class GetTotalIrrigationWaterTest {
  private static EntityManager entityManager;
  private static EntityManagerFactory entityMangerFactory;
  private static IrrigationRecordServiceBean irrigationRecordService;
  private static ParcelServiceBean parcelServiceBean;

  @BeforeClass
  public static void preTest(){
    entityMangerFactory = Persistence.createEntityManagerFactory("swcar");
    entityManager = entityMangerFactory.createEntityManager();

    parcelServiceBean = new ParcelServiceBean();
    parcelServiceBean.setEntityManager(entityManager);

    irrigationRecordService = new IrrigationRecordServiceBean();
    irrigationRecordService.setEntityManager(entityManager);

    System.out.println("Prueba unitaria del método que suma todos los riegos realizados (por parte del usuario cliente para un cultivo dado) en una parcela dada y en una fecha dada");
    System.out.println();
  }

  /*
   * Bloque de codigo fuente de prueba unitaria
   * para el metodo que calcula el agua total
   * utilizada en el riego de un cultivo dado,
   * en una parcela dada, en la fecha actual
   * del sistema
   *
   * *** NOTA ***
   * Para ejecutar esta prueba unitaria
   * y obtener un resultado distinto de 0.0
   * es necesario que en la base de datos
   * existan parcelas y registros de riego
   * los cuales son persistidos ejecutando
   * el comando ant all
   */
  @Test
  public void testGetTotalWaterIrrigation() {
    Parcel choosenParcel = parcelServiceBean.find(1);
    System.out.println("Resultado: " + irrigationRecordService.getTotalWaterIrrigation(choosenParcel));
  }

  @AfterClass
  public static void postTest() {
    // Cierra las conexiones
    entityManager.close();
    entityMangerFactory.close();
  }

}
