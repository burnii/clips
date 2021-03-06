/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import ClipUpdateComponent from '@/entities/clip/clip-update.vue';
import ClipClass from '@/entities/clip/clip-update.component';
import ClipService from '@/entities/clip/clip.service';

import ClipUserService from '@/entities/clip-user/clip-user.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Clip Management Update Component', () => {
    let wrapper: Wrapper<ClipClass>;
    let comp: ClipClass;
    let clipServiceStub: SinonStubbedInstance<ClipService>;

    beforeEach(() => {
      clipServiceStub = sinon.createStubInstance<ClipService>(ClipService);

      wrapper = shallowMount<ClipClass>(ClipUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          clipService: () => clipServiceStub,
          alertService: () => new AlertService(),

          clipUserService: () => new ClipUserService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.clip = entity;
        clipServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(clipServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.clip = entity;
        clipServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(clipServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundClip = { id: 123 };
        clipServiceStub.find.resolves(foundClip);
        clipServiceStub.retrieve.resolves([foundClip]);

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
