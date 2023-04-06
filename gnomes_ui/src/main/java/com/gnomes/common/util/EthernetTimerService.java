package com.gnomes.common.util;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TimerService;
import javax.inject.Inject;

@Singleton
@Startup
public class EthernetTimerService {
    @Resource
    private TimerService timerService;

	@Inject
	private EthernetStore ethernetStore;

	@Schedule(hour="*",minute="*",second="*/10",persistent=false)
	public void timeout(){
		ethernetStore.watchdog();
	}
}
