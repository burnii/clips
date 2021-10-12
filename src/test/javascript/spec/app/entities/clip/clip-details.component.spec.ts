/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import ClipDetailComponent from '@/entities/clip/clip-details.vue';
import ClipClass from '@/entities/clip/clip-details.component';
import ClipService from '@/entities/clip/clip.service';
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
  describe('Clip Management Detail Component', () => {
    let wrapper: Wrapper<ClipClass>;
    let comp: ClipClass;
    let clipServiceStub: SinonStubbedInstance<ClipService>;

    beforeEach(() => {
      clipServiceStub = sinon.createStubInstance<ClipService>(ClipService);

      wrapper = shallowMount<ClipClass>(ClipDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { clipService: () => clipServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundClip = { id: 123 };
        clipServiceStub.find.resolves(foundClip);

        // WHEN
        comp.retrieveClip(123);
        await comp.$nextTick();

        // THEN
        expect(comp.clip).toBe(foundClip);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundClip = { id: 123 };
        clipServiceStub.find.resolves(foundClip);

        // WHEN
        comp.beforeRouteEnter({ params: { clipId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.clip).toBe(foundClip);
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
