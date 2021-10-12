/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';

import * as config from '@/shared/config/config';
import ClipUserComponent from '@/entities/clip-user/clip-user.vue';
import ClipUserClass from '@/entities/clip-user/clip-user.component';
import ClipUserService from '@/entities/clip-user/clip-user.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('b-badge', {});
localVue.directive('b-modal', {});
localVue.component('b-button', {});
localVue.component('router-link', {});

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  describe('ClipUser Management Component', () => {
    let wrapper: Wrapper<ClipUserClass>;
    let comp: ClipUserClass;
    let clipUserServiceStub: SinonStubbedInstance<ClipUserService>;

    beforeEach(() => {
      clipUserServiceStub = sinon.createStubInstance<ClipUserService>(ClipUserService);
      clipUserServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<ClipUserClass>(ClipUserComponent, {
        store,
        i18n,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          clipUserService: () => clipUserServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      clipUserServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllClipUsers();
      await comp.$nextTick();

      // THEN
      expect(clipUserServiceStub.retrieve.called).toBeTruthy();
      expect(comp.clipUsers[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      clipUserServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      comp.removeClipUser();
      await comp.$nextTick();

      // THEN
      expect(clipUserServiceStub.delete.called).toBeTruthy();
      expect(clipUserServiceStub.retrieve.callCount).toEqual(1);
    });
  });
});
