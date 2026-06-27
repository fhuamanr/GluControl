import {afterEach,describe,expect,it,vi} from 'vitest';
import {cleanup,fireEvent,render,screen,waitFor} from '@testing-library/react';
import {BrowserRouter} from 'react-router-dom';
import App from './App';

const jsonResponse=data=>Promise.resolve({ok:true,status:200,json:()=>Promise.resolve(data)});

afterEach(()=>{cleanup();localStorage.clear();vi.restoreAllMocks();window.history.pushState({},'', '/login')});

describe('GluControl por rol',()=>{
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
});

