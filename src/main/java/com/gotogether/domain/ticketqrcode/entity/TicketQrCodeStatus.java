package com.gotogether.domain.ticketqrcode.entity;

public enum TicketQrCodeStatus {
	AVAILABLE(false),
	USED(true);

	private final boolean isCheckIn;

	TicketQrCodeStatus(boolean isCheckIn) {
		this.isCheckIn = isCheckIn;
	}

	public boolean isCheckIn() {
		return isCheckIn;
	}
}