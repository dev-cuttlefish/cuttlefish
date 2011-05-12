package ch.ethz.sg.cuttlefish.misc;

public interface Subject {
	public void addObserver(Observer o);
	public void removeObserver(Observer o);
}
