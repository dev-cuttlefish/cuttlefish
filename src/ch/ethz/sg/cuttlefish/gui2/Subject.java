package ch.ethz.sg.cuttlefish.gui2;

public interface Subject {
	public void addObserver(Observer o);
	public void removeObserver(Observer o);
}
