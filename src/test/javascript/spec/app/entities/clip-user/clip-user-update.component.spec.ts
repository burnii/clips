/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import ClipUserUpdateComponent from '@/entities/clip-user/clip-user-update.vue';
import ClipUserClass from '@/entities/clip-user/clip-user-update.component';
import ClipUserService from '@/entities/clip-user/clip-user.service';

import UserService from '@/admin/user-management/user-management.service';

import ClipService from '@/entities/clip/clip.service';
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
  describe('ClipUser Management Update Component', () => {
    let wrapper: Wrapper<ClipUserClass>;
    let comp: ClipUserClass;
    let clipUserServiceStub: SinonStubbedInstance<ClipUserService>;

    beforeEach(() => {
      clipUserServiceStub = sinon.createStubInstance<ClipUserService>(ClipUserService);

      wrapper = shallowMount<ClipUserClass>(ClipUserUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          clipUserService: () => clipUserServiceStub,
          alertService: () => new AlertService(),

          userService: () => new UserService(),

          clipService: () => new ClipService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.clipUser = entity;
        clipUserServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(clipUserServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.clipUser = entity;
        clipUserServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(clipUserServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundClipUser = { id: 123 };
        clipUserServiceStub.find.resolves(foundClipUser);
        clipUserServiceStub.retrieve.resolves([foundClipUser]);

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
