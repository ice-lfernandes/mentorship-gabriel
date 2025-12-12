 // Interface Observer
    public interface TemperatureObserver {
        void update(double temperature);
    }
    
    // Interface Subject
    public interface TemperatureSubject {
        void registerObserver(TemperatureObserver observer);
        void removeObserver(TemperatureObserver observer);
        void notifyObservers();
    }
    
    // Concrete Subject
    public static class TemperatureSensor implements TemperatureSubject {
        private List<TemperatureObserver> observers;
        private double temperature;
        
        public TemperatureSensor() {
            this.observers = new ArrayList<>();
        }
        
        @Override
        public void registerObserver(TemperatureObserver observer) {
            observers.add(observer);
        }
        
        @Override
        public void removeObserver(TemperatureObserver observer) {
            observers.remove(observer);
        }
        
        @Override
        public void notifyObservers() {
            for (TemperatureObserver observer : observers) {
                observer.update(temperature);
            }
        }
        
        public void setTemperature(double temp) {
            this.temperature = temp;
            notifyObservers();
        }
        
        public double getTemperature() {
            return temperature;
        }
    }
    
    // Concrete Observers
    public static class Display implements TemperatureObserver {
        @Override
        public void update(double temperature) {
            System.out.println("Display: Temperatura atual é " + temperature + "°C");
        }
    }
    
    public static class AlarmSystem implements TemperatureObserver {
        private double threshold = 30.0;
        
        @Override
        public void update(double temperature) {
            if (temperature > threshold) {
                System.out.println("Alarme: TEMPERATURA ALTA! " + temperature + "°C");
            }
        }
    }
    
    public static class Logger implements TemperatureObserver {
        @Override
        public void update(double temperature) {
            System.out.println("Logger: Registrando temperatura " + temperature + "°C");
        }
    }
    
    public static class MobileApp implements TemperatureObserver {
        @Override
        public void update(double temperature) {
            System.out.println("App Mobile: Notificação enviada - Temp: " + temperature + "°C");
        }
    }
    
    public static void main(String[] args) {
        TemperatureSensor sensor = new TemperatureSensor();
        
        // Registrando observers
        Display display = new Display();
        AlarmSystem alarm = new AlarmSystem();
        Logger logger = new Logger();
        MobileApp mobileApp = new MobileApp();
        
        sensor.registerObserver(display);
        sensor.registerObserver(alarm);
        sensor.registerObserver(logger);
        sensor.registerObserver(mobileApp);
        
        System.out.println("=== Mudança de temperatura para 25°C ===");
        sensor.setTemperature(25);
        
        System.out.println("\n=== Mudança de temperatura para 35°C ===");
        sensor.setTemperature(35);
        
        System.out.println("\n=== Removendo o alarme ===");
        sensor.removeObserver(alarm);
        
        System.out.println("\n=== Mudança de temperatura para 40°C ===");
        sensor.setTemperature(40);
    }
}
