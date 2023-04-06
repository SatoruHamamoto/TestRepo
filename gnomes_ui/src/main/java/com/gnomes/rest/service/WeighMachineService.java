package com.gnomes.rest.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.external.data.WeighIfParam;
import com.gnomes.external.data.WeighResult;
import com.gnomes.external.logic.WeighMachine;

@Path("WeighMachineService")
@RequestScoped
public class WeighMachineService {

    /** 秤量器IF機能クラス. */
    @Inject
    protected WeighMachine weighMachine;

    /**
     * 秤量処理.
     * <pre>
     * 秤量器IFパラメータをもとに秤量処理を行う。
     * </pre>
     * @param weighIfParam 秤量器IFパラメータ
     * @return 秤量結果
     */
    @Path("executeWeigh")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @GnomesTransactional
    public WeighResult executeWeigh(WeighIfParam weighIfParam) {

        return weighMachine.executeWeigh(weighIfParam);

    }

}
