package ch.zhaw;

public class Bestenliste {
  private String name;
  private int punkte;
  private long timer;
  private String kategorie;

  public Bestenliste(String name, int punkte, long timer, String kategorie) {
    this.name = name;
    this.punkte = punkte;
    this.timer = timer;
    this.kategorie = kategorie;
  }

  public int getPunkte() {
    return punkte;
  }

  public long getTime() {
    return timer;
  }

  public String getName() {
    return name;
  }

  public String getKategorie() {
    return kategorie;
  }
}
