package com.example.prueba_chart;

public class DatoMQTT {
        private float x; // Atributo X
        private float y; // Atributo Y

        // Constructor
        public DatoMQTT(float x, float y) {
            this.x = x;
            this.y = y;
        }

        // Método para obtener X
        public float getValueX() {
            return x;
        }

        // Método para obtener Y
        public float getValueY() {
            return y;
        }


}
