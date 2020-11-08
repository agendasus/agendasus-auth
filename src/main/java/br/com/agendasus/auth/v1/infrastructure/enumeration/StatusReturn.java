package br.com.agendasus.auth.v1.infrastructure.enumeration;

public enum StatusReturn {

	ERROR(0, "error"), 
	SUCCESS(1, "success"),
	UPDATE(2, "update");

	private int valueInt;
	private String valueString;

	private StatusReturn(int valueInt, String valueString) {
		this.valueInt = valueInt;
		this.valueString = valueString;
	}

	public int getValueInt() {
		return valueInt;
	}
	
	public String getValueString() {
		return valueString;
	}

	public StatusReturn getStatusReturn(Integer value) {
		if (value != null) {
			switch (value) {
			case 0:
				return StatusReturn.ERROR;
			case 1:
				return StatusReturn.SUCCESS;
			case 2:
				return StatusReturn.UPDATE;
			default:
				return null;
			}
		}
		return null;
	}

	public StatusReturn getStatusReturn(String value) {
		if (value != null) {
			switch (value) {
			case "error":
				return StatusReturn.ERROR;
			case "success":
				return StatusReturn.SUCCESS;
			case "update":
				return StatusReturn.UPDATE;
			default:
				return null;
			}
		} 
		return null;
	}

}
