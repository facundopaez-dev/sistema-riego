/*
 * Esta clase tiene implementada las formulas matematicas
 * necesarias para calcular el riego sugerido del dia de
 * hoy y la cantidad de agua acumulada del dia de hoy,
 * ambos valores en milimetros
 */

package irrigation;

import java.lang.Math;

public class WaterMath {

  private WaterMath() {

  }

  /**
   * Calcula el riego sugerido [milimetros] para el dia de hoy en funcion
   * de la ETc, la ETo, la cantidad de agua de lluvia y la cantidad
   * de agua acumulada, siendo todos estos valores del dia de ayer
   *
   * En pocas palabras, calcula el riego sugerido en milimetros para el
   * dia de hoy en funcion de lo que ha sucedido en el dia de ayer
   *
   * @param  hectare                   [hectarea de la parcela sobre la cual estan plantados los cultivos del usuario cliente]
   * @param  yesterdayEtc              [ETc del dia de ayer] [milimetros]
   * @param  yesterdayEto              [ETo del dia de ayer] [milimetros]
   * @param  yesterdayRainWater        [cantidad de agua de lluvia del dia de ayer] [milimetros]
   * @param  waterAccumulatedYesterday [cantidad de agua acumulada del dia de ayer] [milimetros]
   * @return el riego sugerido [milimetros] para el dia de hoy
   */
  public static double getSuggestedIrrigation(double hectare, double yesterdayEtc, double yesterdayEto, double yesterdayRainWater, double waterAccumulatedYesterday,
  double totalIrrigationWaterToday) {
    // public static double getSuggestedIrrigation(double yesterdayEtc, double yesterdayEto, double yesterdayRainWater, double waterAccumulatedYesterday, double totalIrrigationWaterToday) {

    /*
     * Evapotranspiracion del dia de ayer
     */
    double yesterdayEvapotranspiration = 0.0;

    /*
     * Variable que representa el riego sugerido
     * para el dia de hoy, el cual esta en milimetros
     */
    double suggestedIrrigationToday = 0.0;

    /*
     * La ETc es cero cuando no hay un cultivo sembrado
     * en la parcela dada, en cambio es mayor a cero
     * cuando hay un cultivo sembrado en la parcela dada
     *
     * Si la ETc del dia de ayer fue cero (porque no hubo
     * cultivo plantado en el dia de ayer en la parcela
     * dada) se utiliza la ETo del dia de ayer para
     * calcular el riego sugerido para el dia de hoy
     *
     * Si la ETc del dia de ayer no fue cero (porque hubo
     * un cultivo plantado en el dia de ayer en la parcela
     * dada), entonces se la utiliza para calcular el riego
     * sugerido para el dia de hoy
     */
    if (yesterdayEtc == 0.0) {
      yesterdayEvapotranspiration = yesterdayEto;
    } else {
      yesterdayEvapotranspiration = yesterdayEtc;
    }

    /*
     * Si la evapotranspiracion del dia de ayer es mayor que la suma entre
     * la cantidad de agua de lluvia del dia de ayer, la cantidad de agua
     * acumulada del dia de ayer y la cantidad total de agua utilizada para
     * el riego en el dia de hoy, entonces el riego sugerido para el dia de hoy
     * el riego sugerido para el dia de hoy es igual a la evapotranspiracion del
     * dia de ayer menos la suma entre la cantidad de agua de lluvia del dia de
     * ayer, la cantidad de agua acumulada del dia de ayer y la cantidad total
     * de agua utilizada para el riego en el dia de hoy
     *
     * Si la cantidad de agua de lluvia del dia de ayer mas la cantidad
     * de agua acumulada del dia de ayer mas la cantidad total de agua utilizada
     * para el riego en el dia de hoy es mayor o igual que la evapotranspiracion
     * del dia de ayer, entonces el riego sugerido para el dia de hoy es cero porque
     * la cantidad de agua que expresa la evapotranspiracion del dia de ayer ya esta
     * suplida por la suma de las cantidades de agua mencionadas
     */
    // if (yesterdayEvapotranspiration > (yesterdayRainWater + waterAccumulatedYesterday + totalIrrigationWaterToday)) {
    //   suggestedIrrigationToday = yesterdayEvapotranspiration - (yesterdayRainWater + waterAccumulatedYesterday + totalIrrigationWaterToday);
    // }
    //
    // suggestedIrrigationToday = hectare * suggestedIrrigationToday;

    /*
     * Necesidad total de riego del cultivo dado
     *
     * Este valor es igual a la multiplicacion
     * entre la cantidad de hectareas de la parcela
     * dada y la necesidad de riego del cultivo dado, la
     * cual es igual a la evapotranspiracion del dia
     * de ayer menos la suma entre la cantidad de agua
     * de lluvia del dia de ayer y la cantidad de agua
     * acumulada del dia de ayer
     */
    double totalNeedIrrigation = hectare * (yesterdayEvapotranspiration - (yesterdayRainWater + waterAccumulatedYesterday));

    if (totalNeedIrrigation > (yesterdayRainWater + waterAccumulatedYesterday + totalIrrigationWaterToday)) {
      suggestedIrrigationToday = totalNeedIrrigation - (yesterdayRainWater + waterAccumulatedYesterday + totalIrrigationWaterToday);
    }

    return truncateToThreeDecimals(suggestedIrrigationToday);
  }

  /**
   * Calcula la cantidad de agua acumulada [milimetros] del dia hoy
   * en funcion de la ETc, la ETo (en caso de que la ETc sea cero, lo cual
   * se debe a que no hubo un cultivo sembrado en el dia de ayer en la
   * parcela dada), la cantidad de agua de lluvia, la cantidad de agua
   * acumulada (siendo todos estos valores del dia de ayer) y la cantidad
   * total de agua de riego utilizada en el dia de hoy
   *
   * @param  yesterdayEtc              [ETc del dia de ayer] [milimetros]
   * @param  yesterdayEto              [ETo del dia de ayer] [milimetros]
   * @param  yesterdayRainWater        [cantidad de agua de lluvia del dia de ayer] [milimetros]
   * @param  waterAccumulatedYesterday [cantidad de agua acumulada del dia de ayer] [milimetros]
   * @param  totalIrrigationWaterToday [cantidad total de agua utilizada en los riegos del dia de hoy] [milimetros]
   * @return la cantidad de agua [milimetros] acumulada en el dia
   * de hoy, la cual es la cantidad de agua a favor del dia de hoy
   * para el dia de mañana
   */
  public static double getWaterAccumulatedToday(double yesterdayEtc, double yesterdayEto, double yesterdayRainWater,
  double waterAccumulatedYesterday, double totalIrrigationWaterToday) {

    double yesterdayEvapotranspiration = 0.0;
    double waterAccumulatedToday = 0.0;

    /*
     * La ETc es cero cuando no hay un cultivo sembrado
     * en la parcela dada, en cambio es mayor a cero
     * cuando hay un cultivo sembrado en la parcela dada
     *
     * Si la ETc del dia de ayer fue cero (porque no hubo
     * cultivo plantado en el dia de ayer en la parcela
     * dada) se utiliza la ETo del dia de ayer para
     * calcular la cantidad de agua acumulada del dia
     * de hoy, la cual es agua a favor del dia de hoy
     * para el dia de mañana
     *
     * Si la ETc del dia de ayer no fue cero (porque hubo
     * un cultivo plantado en el dia de ayer en la parcela
     * dada), entonces se la utiliza para calcular la cantidad
     * de agua acumulada del dia de hoy, la cual es agua a favor
     * del dia de hoy para el dia de mañana
     */
    if (yesterdayEtc == 0.0) {
      yesterdayEvapotranspiration = yesterdayEto;
    } else {
      yesterdayEvapotranspiration = yesterdayEtc;
    }

    /*
     * Si el agua de lluvia del dia de ayer mas la cantidad de agua
     * acumulada del dia de ayer mas la cantidad total de agua utilizada
     * en el riego del dia de hoy es mayor que la evapotranspiracion del
     * dia de ayer, entonces la cantidad de agua acumulada [milimetros]
     * del dia de hoy (la cual es agua a favor para mañana) es la
     * diferencia entre la suma de las cantidades de agua mencionadas
     * y la evapotranspiracion del dia de ayer
     */
    if ((yesterdayRainWater + waterAccumulatedYesterday + totalIrrigationWaterToday) > yesterdayEvapotranspiration) {
      waterAccumulatedToday = (yesterdayRainWater + waterAccumulatedYesterday + totalIrrigationWaterToday) - yesterdayEvapotranspiration;
    }

    return waterAccumulatedToday;
  }

  /**
   * @param  givenNumber numero con varias cifras despues del punto decimal
   * @return numero con una parte entera y tres cifras decimales
   */
  public static double truncateToThreeDecimals(double givenNumber) {
    /*
     * Multiplica el numero decimal por 1000 y toma
     * la parte entera del resultado de la multiplicacion,
     * lo cual se logra mediante el casteo explicito
     */
    int wholeNumber = ((int) (givenNumber * 1000));

    /*
     * El numero entero resultante de la operacion anterior
     * es dividido por 1000.0 (double) para tener un numero
     * con una parte entera y tres cifras decimales
     */
    return (wholeNumber / 1000.0);
  }

}
