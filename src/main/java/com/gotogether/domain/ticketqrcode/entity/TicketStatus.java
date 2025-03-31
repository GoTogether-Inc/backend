package com.gotogether.domain.ticketqrcode.entity;

public enum TicketStatus {
	AVAILABLE(false),
	USED(true);

	private final boolean isCheckIn;

	TicketStatus(boolean isCheckIn) {
		this.isCheckIn = isCheckIn;
	}

	public boolean isCheckIn() {
		return isCheckIn;
	}
}