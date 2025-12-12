# Exercício 3: Observer
### Cenário: Sistema de monitoramento de temperatura que notifica diferentes dispositivos quando a temperatura muda.

```java
// Implementação SEM Observer Pattern
public class TemperatureMonitor {
    
    public static class TemperatureSensor {
        private double temperature;
        
        // Referencias diretas - alto acoplamento
        private Display display;
        private AlarmSystem alarmSystem;
        private Logger logger;
        
        public TemperatureSensor(Display display, AlarmSystem alarmSystem, Logger logger) {
            this.display = display;
            this.alarmSystem = alarmSystem;
            this.logger = logger;
        }
        
        public void setTemperature(double temp) {
            this.temperature = temp;
            
            // Chamadas diretas e acopladas
            display.updateDisplay(temperature);
            alarmSystem.checkAlarm(temperature);
            logger.logTemperature(temperature);
        }
        
        public double getTemperature() {
            return temperature;
        }
    }
    
    public static class Display {
        public void updateDisplay(double temp) {
            System.out.println("Display: Temperatura atual é " + temp + "°C");
        }
    }
    
    public static class AlarmSystem {
        public void checkAlarm(double temp) {
            if (temp > 30) {
                System.out.println("Alarme: TEMPERATURA ALTA! " + temp + "°C");
            }
        }
    }
    
    public static class Logger {
        public void logTemperature(double temp) {
            System.out.println("Logger: Registrando temperatura " + temp + "°C");
        }
    }
    
    public static void main(String[] args) {
        Display display = new Display();
        AlarmSystem alarm = new AlarmSystem();
        Logger logger = new Logger();
        
        TemperatureSensor sensor = new TemperatureSensor(display, alarm, logger);
        
        System.out.println("=== Mudança de temperatura para 25°C ===");
        sensor.setTemperature(25);
        
        System.out.println("\n=== Mudança de temperatura para 35°C ===");
        sensor.setTemperature(35);
    }
}
```
