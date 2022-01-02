package fr.atesab.xray.color;

public interface IColorObject {
	static final IColorObject EMPTY = new IColorObject() {
		@Override
		public int getColor() {
			return 0;
		}

		@Override
		public String getModeName() {
			return "";
		}
	};

	int getColor();

	String getModeName();
}
