/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import ClipUserDetailComponent from '@/entities/clip-user/clip-user-details.vue';
import ClipUserClass from '@/entities/clip-user/clip-user-details.component';
import ClipUserService from '@/entities/clip-user/clip-user.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('ClipUser Management Detail Component', () => {
    let wrapper: Wrapper<ClipUserClass>;
    let comp: ClipUserClass;
    let clipUserServiceStub: SinonStubbedInstance<ClipUserService>;

    beforeEach(() => {
      clipUserServiceStub = sinon.createStubInstance<ClipUserService>(ClipUserService);

      wrapper = shallowMount<ClipUserClass>(ClipUserDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { clipUserService: () => clipUserServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundClipUser = { id: 123 };
        clipUserServiceStub.find.resolves(foundClipUser);

        // WHEN
        comp.retrieveClipUser(123);
        await comp.$nextTick();

        // THEN
        expect(comp.clipUser).toBe(foundClipUser);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundClipUser = { id: 123 };
        clipUserServiceStub.find.resolves(foundClipUser);

        // WHEN
        comp.beforeRouteEnter({ params: { clipUserId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.clipUser).toBe(foundClipUser);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
