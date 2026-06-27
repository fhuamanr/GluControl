import {afterEach,describe,expect,it,vi} from 'vitest';
import {cleanup,fireEvent,render,screen,waitFor} from '@testing-library/react';
import {BrowserRouter} from 'react-router-dom';
import App from './App';

const jsonResponse=data=>Promise.resolve({ok:true,status:200,json:()=>Promise.resolve(data)});

afterEach(()=>{cleanup();localStorage.clear();vi.restoreAllMocks();window.history.pushState({},'', '/login')});

describe('GluControl por rol',()=>{
  it('ofrece registro público y crea una sesión de paciente',async()=>{
    window.history.pushState({},'', '/register');
    vi.spyOn(globalThis,'fetch')
      .mockImplementationOnce(()=>jsonResponse({userId:9,patientId:9,fullName:'Ana Torres',email:'ana@example.com',role:'PATIENT',token:'jwt-ana'}))
      .mockImplementationOnce(()=>jsonResponse({content:[]}))
      .mockImplementationOnce(()=>jsonResponse([]));
    const {container}=render(<BrowserRouter><App/></BrowserRouter>);
    fireEvent.change(screen.getByLabelText('Nombres'),{target:{value:'Ana'}});
    fireEvent.change(screen.getByLabelText('Apellidos'),{target:{value:'Torres'}});
    fireEvent.change(screen.getByLabelText('Correo electrónico'),{target:{value:'ana@example.com'}});
    fireEvent.change(container.querySelector('input[type="password"]'),{target:{value:'segura123'}});
    fireEvent.click(screen.getByRole('button',{name:'Crear mi cuenta'}));
    expect(await screen.findByRole('heading',{name:'Hola, Ana'})).toBeInTheDocument();
    expect(JSON.parse(localStorage.getItem('glucontrol-session')).patientId).toBe(9);
  });

  it('muestra el acceso y completa el flujo principal del paciente',async()=>{
    const fetchMock=vi.spyOn(globalThis,'fetch')
      .mockImplementationOnce(()=>jsonResponse({userId:1,patientId:1,fullName:'Javier Mendoza',email:'paciente@glucontrol.pe',role:'PATIENT',token:'jwt-demo'}))
      .mockImplementationOnce(()=>jsonResponse({content:[{id:1,valueMgDl:105,measuredAt:'2026-06-27T13:00:00Z',context:'AFTER_MEAL',rangeStatus:'IN_RANGE'}]}))
      .mockImplementationOnce(()=>jsonResponse([{id:1,name:'Metformina',dose:'850 mg',frequency:'2 veces al día',reminderTime:'20:00:00'}]));
    render(<BrowserRouter><App/></BrowserRouter>);
    expect(screen.getByRole('heading',{name:'Tu salud, bajo control'})).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button',{name:'Iniciar sesión'}));
    expect(await screen.findByRole('heading',{name:'Hola, Javier'})).toBeInTheDocument();
    expect(screen.getByText('105')).toBeInTheDocument();
    expect(JSON.parse(localStorage.getItem('glucontrol-session')).token).toBe('jwt-demo');
    await waitFor(()=>expect(fetchMock).toHaveBeenCalledTimes(3));
    expect(fetchMock.mock.calls[1][1].headers.Authorization).toBe('Bearer jwt-demo');
  });

  it('renderiza el panel clínico para el rol médico',async()=>{
    localStorage.setItem('glucontrol-session',JSON.stringify({userId:2,fullName:'Dra. Elena Rojas',role:'DOCTOR',token:'jwt-medico'}));
    window.history.pushState({},'', '/doctor');
    vi.spyOn(globalThis,'fetch').mockImplementation(()=>jsonResponse({activePatients:2,criticalAlerts:3,pendingFollowUps:4,averageHba1c:6.8,patients:[]}));
    render(<BrowserRouter><App/></BrowserRouter>);
    expect(await screen.findByRole('heading',{name:'Buenos días, doctora'})).toBeInTheDocument();
    expect(screen.getByText('Pacientes activos')).toBeInTheDocument();
    expect(screen.getByText('Alertas críticas')).toBeInTheDocument();
  });

  it('sube una foto y guarda su referencia en una comida',async()=>{
    localStorage.setItem('glucontrol-session',JSON.stringify({userId:1,patientId:1,fullName:'Javier Mendoza',role:'PATIENT',token:'jwt-paciente'}));
    window.history.pushState({},'', '/meals');
    const fetchMock=vi.spyOn(globalThis,'fetch')
      .mockImplementationOnce(()=>jsonResponse({content:[]}))
      .mockImplementationOnce(()=>jsonResponse({url:'/api/uploads/plato.jpg'}))
      .mockImplementationOnce(()=>jsonResponse({id:20,name:'Ensalada',photoUrl:'/api/uploads/plato.jpg'}))
      .mockImplementationOnce(()=>jsonResponse({content:[]}));
    render(<BrowserRouter><App/></BrowserRouter>);
    expect(await screen.findByRole('heading',{name:'Alimentación'})).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button',{name:'Agregar comida'}));
    fireEvent.change(screen.getByLabelText('Nombre del plato'),{target:{value:'Ensalada'}});
    const file=new File(['imagen'],'plato.jpg',{type:'image/jpeg'});
    fireEvent.change(screen.getByLabelText('Subir imagen'),{target:{files:[file]}});
    expect(screen.getByAltText('Vista previa del plato')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button',{name:'Guardar alimento'}));
    await waitFor(()=>expect(fetchMock).toHaveBeenCalledTimes(4));
    expect(fetchMock.mock.calls[1][1].body).toBeInstanceOf(FormData);
    expect(JSON.parse(fetchMock.mock.calls[2][1].body).photoUrl).toBe('/api/uploads/plato.jpg');
  });
});
