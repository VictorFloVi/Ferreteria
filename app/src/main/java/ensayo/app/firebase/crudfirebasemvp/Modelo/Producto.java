package ensayo.app.firebase.crudfirebasemvp.Modelo;

public class Producto {
    private String nombre;
    private String categoria;
    private double costo;
    private double precioVenta;
    private String imagenUrl;

    public Producto(String nombre, String categoria, double costo, double precioVenta, String imagenUrl) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.costo = costo;
        this.precioVenta = precioVenta;
        this.imagenUrl = imagenUrl;
    }

    public Producto(String nombre, String imagenUrl) {
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
