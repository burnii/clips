/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';

import * as config from '@/shared/config/config';
import ClipComponent from '@/entities/clip/clip.vue';
import ClipClass from '@/entities/clip/clip.component';
import ClipService from '@/entities/clip/clip.service';
import AlertService from '@/shared/alert/alert.service';
import ClipUserService from '@/entities/clip-user/clip-user.service';

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
  describe('Clip Management Component', () => {
    let wrapper: Wrapper<ClipClass>;
    let comp: ClipClass;
    let clipServiceStub: SinonStubbedInstance<ClipService>;
    let clipUserServiceStub: SinonStubbedInstance<ClipUserService>;

    beforeEach(() => {
      clipServiceStub = sinon.createStubInstance<ClipService>(ClipService);
      clipServiceStub.retrieve.resolves({ headers: {} });

      clipUserServiceStub = sinon.createStubInstance<ClipUserService>(ClipUserService);
      clipUserServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<ClipClass>(ClipComponent, {
        store,
        i18n,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          clipService: () => clipServiceStub,
          clipUserService: () => clipUserServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      clipServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllClips();
      await comp.$nextTick();

      // THEN
      expect(clipServiceStub.retrieve.called).toBeTruthy();
      expect(comp.clips[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      clipServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      comp.removeClip();
      await comp.$nextTick();

      // THEN
      expect(clipServiceStub.delete.called).toBeTruthy();
      expect(clipServiceStub.retrieve.callCount).toEqual(1);
    });
  });
});
