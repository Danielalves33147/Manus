import React, { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button.jsx'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card.jsx'
import { Input } from '@/components/ui/input.jsx'
import { Label } from '@/components/ui/label.jsx'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs.jsx'
import { Badge } from '@/components/ui/badge.jsx'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select.jsx'
import { Textarea } from '@/components/ui/textarea.jsx'
import { Heart, Package, Users, ShoppingCart, UserPlus, Trash2, Edit, AlertCircle, Bell, Settings } from 'lucide-react'
import './App.css'

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [userRole, setUserRole] = useState('')
  const [currentUser, setCurrentUser] = useState('')
  const [loginData, setLoginData] = useState({ username: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  
  // Estados para cestas básicas
  const [cestas, setCestas] = useState([])
  const [novaCesta, setNovaCesta] = useState('')
  const [modeloSelecionado, setModeloSelecionado] = useState('')
  
  // Estados para modelos de cestas
  const [modelos, setModelos] = useState([])
  const [novoModelo, setNovoModelo] = useState({
    nome: '',
    descricao: '',
    itens: []
  })
  const [itemModelo, setItemModelo] = useState({
    tipoAlimento: '',
    quantidade: '',
    unidadeMedida: 'kg'
  })
  
  // Estados para beneficiados
  const [beneficiados, setBeneficiados] = useState([])
  const [novoBeneficiado, setNovoBeneficiado] = useState({
    nome: '',
    cpf: '',
    telefone: '',
    endereco: '',
    dataNascimento: '',
    rendaFamiliar: '',
    numeroDependentes: ''
  })
  
  // Estados para doações
  const [doacoes, setDoacoes] = useState([])
  const [novaDoacao, setNovaDoacao] = useState({
    tipoAlimento: '',
    quantidade: '',
    dataRecebimento: '',
    dataValidade: '',
    origem: '',
    observacoes: ''
  })
  
  // Estados para usuários (admin)
  const [usuarios, setUsuarios] = useState([])
  const [novoUsuario, setNovoUsuario] = useState({
    username: '',
    password: '',
    email: '',
    role: 'USER'
  })

  // Estados para estatísticas
  const [stats, setStats] = useState({
    totalCestas: 0,
    totalBeneficiados: 0,
    totalDoacoes: 0,
    totalUsuarios: 0
  })

  // Estados para alertas
  const [alertas, setAlertas] = useState({
    dashboard: null,
    vencimento: [],
    estoqueBaixo: [],
    beneficiados: []
  })
  const [loadingAlertas, setLoadingAlertas] = useState(false)

  const API_BASE = 'http://localhost:8080/api'

  // Função para fazer requisições simples (sem JWT)
  const fetchAPI = async (url, options = {}) => {
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    }
    
    try {
      const response = await fetch(url, config)
      return response
    } catch (error) {
      console.error('Erro na requisição:', error)
      throw error
    }
  }

  // Verificar se há sessão salva ao carregar a página
  useEffect(() => {
    const savedUser = localStorage.getItem('currentUser')
    const savedRole = localStorage.getItem('userRole')
    if (savedUser && savedRole) {
      setIsLoggedIn(true)
      setUserRole(savedRole)
      setCurrentUser(savedUser)
      carregarDadosIniciais(savedRole)
    }
  }, [])

  const carregarDadosIniciais = async (role) => {
    try {
      await Promise.all([
        carregarCestas(),
        carregarModelos(),
        carregarBeneficiados(),
        carregarDoacoes(),
        carregarEstatisticas(),
        carregarAlertas()
      ])
      
      if (role === 'ADMIN') {
        await carregarUsuarios()
      }
    } catch (error) {
      console.error('Erro ao carregar dados iniciais:', error)
    }
  }

  const handleLogin = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    
    try {
      const response = await fetchAPI(`${API_BASE}/auth/login`, {
        method: 'POST',
        body: JSON.stringify(loginData),
      })
      
      if (response.ok) {
        const data = await response.json()
        if (data.success) {
          localStorage.setItem('currentUser', data.username)
          localStorage.setItem('userRole', data.role)
          setUserRole(data.role)
          setCurrentUser(data.username)
          setIsLoggedIn(true)
          
          // Carregar dados iniciais
          await carregarDadosIniciais(data.role)
        } else {
          setError(data.message || 'Erro no login')
        }
      } else {
        const errorData = await response.json()
        setError(errorData.message || 'Erro no login')
      }
    } catch (error) {
      console.error('Erro no login:', error)
      setError('Erro de conexão com o servidor.')
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('currentUser')
    localStorage.removeItem('userRole')
    setIsLoggedIn(false)
    setUserRole('')
    setCurrentUser('')
    setCestas([])
    setBeneficiados([])
    setDoacoes([])
    setUsuarios([])
    setStats({ totalCestas: 0, totalBeneficiados: 0, totalDoacoes: 0, totalUsuarios: 0 })
  }

  // Funções para cestas básicas
  const criarCesta = async () => {
    if (!novaCesta.trim()) {
      setError('Nome da cesta é obrigatório')
      return
    }

    if (!modeloSelecionado) {
      setError('Selecione um modelo de cesta')
      return
    }
    
    setLoading(true)
    try {
      const response = await fetchAPI(`${API_BASE}/cestas-basicas/criar-com-modelo`, {
        method: 'POST',
        body: JSON.stringify({ 
          nomeCesta: novaCesta,
          preDefinicaoId: modeloSelecionado
        }),
      })
      
      if (response.ok) {
        const cesta = await response.json()
        setCestas([...cestas, cesta])
        setNovaCesta('')
        setModeloSelecionado('')
        setError('')
        await carregarEstatisticas()
        await carregarAlertas() // Atualizar alertas após criar cesta
      } else {
        const errorText = await response.text()
        setError('Erro ao criar cesta: ' + errorText)
      }
    } catch (error) {
      setError('Erro ao criar cesta: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  const carregarCestas = async () => {
    try {
      const response = await fetchAPI(`${API_BASE}/cestas-basicas`)
      
      if (response.ok) {
        const data = await response.json()
        setCestas(Array.isArray(data) ? data : [])
      }
    } catch (error) {
      console.error('Erro ao carregar cestas:', error)
    }
  }

  // Funções para modelos de cestas
  const carregarModelos = async () => {
    try {
      const response = await fetchAPI(`${API_BASE}/pre-definicoes`)
      
      if (response.ok) {
        const data = await response.json()
        setModelos(Array.isArray(data) ? data : [])
      }
    } catch (error) {
      console.error('Erro ao carregar modelos:', error)
    }
  }

  const criarModelo = async () => {
    if (!novoModelo.nome || novoModelo.itens.length === 0) {
      setError('Nome do modelo e pelo menos um item são obrigatórios')
      return
    }
    
    setLoading(true)
    try {
      const response = await fetchAPI(`${API_BASE}/pre-definicoes`, {
        method: 'POST',
        body: JSON.stringify(novoModelo),
      })
      
      if (response.ok) {
        const modelo = await response.json()
        setModelos([...modelos, modelo])
        setNovoModelo({
          nome: '',
          descricao: '',
          itens: []
        })
        setError('')
      } else {
        const errorText = await response.text()
        setError('Erro ao criar modelo: ' + errorText)
      }
    } catch (error) {
      setError('Erro ao criar modelo: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  const adicionarItemModelo = () => {
    if (!itemModelo.tipoAlimento || !itemModelo.quantidade) {
      setError('Tipo de alimento e quantidade são obrigatórios')
      return
    }

    const novoItem = {
      tipoAlimento: itemModelo.tipoAlimento,
      quantidade: parseFloat(itemModelo.quantidade),
      unidadeMedida: itemModelo.unidadeMedida
    }

    setNovoModelo({
      ...novoModelo,
      itens: [...novoModelo.itens, novoItem]
    })

    setItemModelo({
      tipoAlimento: '',
      quantidade: '',
      unidadeMedida: 'kg'
    })
    setError('')
  }

  const removerItemModelo = (index) => {
    const novosItens = novoModelo.itens.filter((_, i) => i !== index)
    setNovoModelo({
      ...novoModelo,
      itens: novosItens
    })
  }

  // Funções para beneficiados
  const criarBeneficiado = async () => {
    if (!novoBeneficiado.nome || !novoBeneficiado.cpf) {
      setError('Nome e CPF são obrigatórios')
      return
    }
    
    setLoading(true)
    try {
      const response = await fetchAPI(`${API_BASE}/beneficiados`, {
        method: 'POST',
        body: JSON.stringify(novoBeneficiado),
      })
      
      if (response.ok) {
        const beneficiado = await response.json()
        setBeneficiados([...beneficiados, beneficiado])
        setNovoBeneficiado({
          nome: '',
          cpf: '',
          telefone: '',
          endereco: '',
          dataNascimento: '',
          rendaFamiliar: '',
          numeroDependentes: ''
        })
        setError('')
        await carregarEstatisticas()
      } else {
        const errorText = await response.text()
        setError('Erro ao cadastrar beneficiado: ' + errorText)
      }
    } catch (error) {
      setError('Erro ao cadastrar beneficiado: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  const carregarBeneficiados = async () => {
    try {
      const response = await fetchAPI(`${API_BASE}/beneficiados`)
      
      if (response.ok) {
        const data = await response.json()
        setBeneficiados(Array.isArray(data) ? data : [])
      }
    } catch (error) {
      console.error('Erro ao carregar beneficiados:', error)
    }
  }

  // Funções para doações
  const criarDoacao = async () => {
    if (!novaDoacao.tipoAlimento || !novaDoacao.quantidade) {
      setError('Tipo de alimento e quantidade são obrigatórios')
      return
    }
    
    setLoading(true)
    try {
      const response = await fetchAPI(`${API_BASE}/doacoes`, {
        method: 'POST',
        body: JSON.stringify({
          ...novaDoacao,
          unidadeMedida: 'kg'
        }),
      })
      
      if (response.ok) {
        const doacao = await response.json()
        setDoacoes([...doacoes, doacao])
        setNovaDoacao({
          tipoAlimento: '',
          quantidade: '',
          dataRecebimento: '',
          dataValidade: '',
          origem: '',
          observacoes: ''
        })
        setError('')
        await carregarEstatisticas()
      } else {
        const errorText = await response.text()
        setError('Erro ao registrar doação: ' + errorText)
      }
    } catch (error) {
      setError('Erro ao registrar doação: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  const carregarDoacoes = async () => {
    try {
      const response = await fetchAPI(`${API_BASE}/doacoes`)
      
      if (response.ok) {
        const data = await response.json()
        setDoacoes(Array.isArray(data) ? data : [])
      }
    } catch (error) {
      console.error('Erro ao carregar doações:', error)
    }
  }

  // Funções para usuários (admin)
  const criarUsuario = async () => {
    if (!novoUsuario.username || !novoUsuario.password) {
      setError('Username e senha são obrigatórios')
      return
    }
    
    setLoading(true)
    try {
      const response = await fetchAPI(`${API_BASE}/users`, {
        method: 'POST',
        body: JSON.stringify(novoUsuario),
      })
      
      if (response.ok) {
        const usuario = await response.json()
        setUsuarios([...usuarios, usuario])
        setNovoUsuario({
          username: '',
          password: '',
          email: '',
          role: 'USER'
        })
        setError('')
        await carregarEstatisticas()
      } else {
        const errorText = await response.text()
        setError('Erro ao criar usuário: ' + errorText)
      }
    } catch (error) {
      setError('Erro ao criar usuário: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  const carregarUsuarios = async () => {
    try {
      const response = await fetchAPI(`${API_BASE}/users`)
      
      if (response.ok) {
        const data = await response.json()
        setUsuarios(Array.isArray(data) ? data : [])
      }
    } catch (error) {
      console.error('Erro ao carregar usuários:', error)
    }
  }

  // Função para carregar estatísticas
  const carregarEstatisticas = async () => {
    try {
      setStats({
        totalCestas: cestas.length,
        totalBeneficiados: beneficiados.length,
        totalDoacoes: doacoes.length,
        totalUsuarios: usuarios.length
      })
    } catch (error) {
      console.error('Erro ao carregar estatísticas:', error)
    }
  }

  // Função para carregar alertas
  const carregarAlertas = async () => {
    setLoadingAlertas(true)
    try {
      // Carregar dashboard de alertas
      const dashboardResponse = await fetchAPI(`${API_BASE}/api/alertas/dashboard`)
      if (dashboardResponse.ok) {
        const dashboardData = await dashboardResponse.json()
        setAlertas(prev => ({ ...prev, dashboard: dashboardData }))
      }

      // Carregar alertas de vencimento
      const vencimentoResponse = await fetchAPI(`${API_BASE}/api/alertas/vencimento`)
      if (vencimentoResponse.ok) {
        const vencimentoData = await vencimentoResponse.json()
        setAlertas(prev => ({ ...prev, vencimento: vencimentoData }))
      }

      // Carregar alertas de estoque baixo
      const estoqueResponse = await fetchAPI(`${API_BASE}/api/alertas/estoque-baixo`)
      if (estoqueResponse.ok) {
        const estoqueData = await estoqueResponse.json()
        setAlertas(prev => ({ ...prev, estoqueBaixo: estoqueData }))
      }

      // Carregar alertas de beneficiados
      const beneficiadosResponse = await fetchAPI(`${API_BASE}/api/alertas/beneficiados-inativos`)
      if (beneficiadosResponse.ok) {
        const beneficiadosData = await beneficiadosResponse.json()
        setAlertas(prev => ({ ...prev, beneficiados: beneficiadosData }))
      }
    } catch (error) {
      console.error('Erro ao carregar alertas:', error)
    } finally {
      setLoadingAlertas(false)
    }
  }

  // Função para exportar relatórios
  const exportarRelatorio = async (tipo) => {
    setLoading(true)
    try {
      let dados = []
      let nomeArquivo = ''
      
      switch (tipo) {
        case 'beneficiados':
          dados = beneficiados
          nomeArquivo = 'relatorio_beneficiados.json'
          break
        case 'doacoes':
          dados = doacoes
          nomeArquivo = 'relatorio_doacoes.json'
          break
        case 'distribuicoes':
          dados = cestas
          nomeArquivo = 'relatorio_distribuicoes.json'
          break
        default:
          setError('Tipo de relatório inválido')
          return
      }
      
      const blob = new Blob([JSON.stringify(dados, null, 2)], { type: 'application/json' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = nomeArquivo
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      window.URL.revokeObjectURL(url)
      
      setError('')
    } catch (error) {
      setError('Erro ao exportar relatório: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  if (!isLoggedIn) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <div className="mx-auto mb-4 w-12 h-12 bg-red-100 rounded-full flex items-center justify-center">
              <Heart className="w-6 h-6 text-red-600" />
            </div>
            <CardTitle className="text-2xl font-bold text-gray-900">Sistema de Doações</CardTitle>
            <CardDescription>Faça login para gerenciar cestas básicas</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleLogin} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username">Usuário</Label>
                <Input
                  id="username"
                  type="text"
                  placeholder="Digite seu usuário"
                  value={loginData.username}
                  onChange={(e) => setLoginData({...loginData, username: e.target.value})}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="password">Senha</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="Digite sua senha"
                  value={loginData.password}
                  onChange={(e) => setLoginData({...loginData, password: e.target.value})}
                  required
                />
              </div>
              
              {error && (
                <div className="flex items-center space-x-2 text-red-600 text-sm">
                  <AlertCircle className="w-4 h-4" />
                  <span>{error}</span>
                </div>
              )}
              
              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? 'Entrando...' : 'Entrar'}
              </Button>
            </form>
            
            <div className="mt-6 p-4 bg-blue-50 rounded-lg">
              <p className="text-sm text-blue-800 font-medium">Credenciais de teste:</p>
              <p className="text-sm text-blue-600">Usuário: admin</p>
              <p className="text-sm text-blue-600">Senha: admin123</p>
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-3">
              <Heart className="w-8 h-8 text-red-600" />
              <h1 className="text-xl font-bold text-gray-900">Sistema de Doações</h1>
              <Badge variant="secondary">{userRole}</Badge>
              <span className="text-sm text-gray-600">Olá, {currentUser}!</span>
            </div>
            <Button variant="outline" onClick={handleLogout}>
              Sair
            </Button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {error && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-center space-x-2">
            <AlertCircle className="w-5 h-5 text-red-600" />
            <span className="text-red-800">{error}</span>
            <Button variant="ghost" size="sm" onClick={() => setError('')}>×</Button>
          </div>
        )}

        <Tabs defaultValue="cestas" className="space-y-6">
          <TabsList className="grid w-full grid-cols-7 lg:grid-cols-8">
            <TabsTrigger value="cestas" className="flex items-center space-x-2">
              <Package className="w-4 h-4" />
              <span>Cestas Básicas</span>
            </TabsTrigger>
            <TabsTrigger value="modelos" className="flex items-center space-x-2">
              <Settings className="w-4 h-4" />
              <span>Modelos</span>
            </TabsTrigger>
            <TabsTrigger value="beneficiados" className="flex items-center space-x-2">
              <Users className="w-4 h-4" />
              <span>Beneficiados</span>
            </TabsTrigger>
            <TabsTrigger value="doacoes" className="flex items-center space-x-2">
              <ShoppingCart className="w-4 h-4" />
              <span>Doações</span>
            </TabsTrigger>
            <TabsTrigger value="alertas" className="flex items-center space-x-2">
              <Bell className="w-4 h-4" />
              <span>Alertas</span>
              {alertas.dashboard && alertas.dashboard.totalAlertas > 0 && (
                <Badge variant="destructive" className="ml-1 text-xs">
                  {alertas.dashboard.totalAlertas}
                </Badge>
              )}
            </TabsTrigger>
            <TabsTrigger value="relatorios" className="flex items-center space-x-2">
              <Heart className="w-4 h-4" />
              <span>Relatórios</span>
            </TabsTrigger>
            {userRole === 'ADMIN' && (
              <TabsTrigger value="usuarios" className="flex items-center space-x-2">
                <UserPlus className="w-4 h-4" />
                <span>Usuários</span>
              </TabsTrigger>
            )}
          </TabsList>

          {/* Aba Cestas Básicas */}
          <TabsContent value="cestas">
            <Card>
              <CardHeader>
                <CardTitle>Gerenciar Cestas Básicas</CardTitle>
                <CardDescription>Crie cestas básicas a partir de modelos pré-definidos</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="grid gap-4 md:grid-cols-2">
                  <div>
                    <Label htmlFor="nome-cesta">Nome da Cesta *</Label>
                    <Input
                      id="nome-cesta"
                      placeholder="Ex: Cesta Família Silva"
                      value={novaCesta}
                      onChange={(e) => setNovaCesta(e.target.value)}
                    />
                  </div>
                  <div>
                    <Label htmlFor="modelo-cesta">Modelo da Cesta *</Label>
                    <Select value={modeloSelecionado} onValueChange={setModeloSelecionado}>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione um modelo" />
                      </SelectTrigger>
                      <SelectContent>
                        {modelos.map((modelo) => (
                          <SelectItem key={modelo.id} value={modelo.id}>
                            {modelo.nome} - {modelo.itens?.length || 0} itens
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                </div>
                
                <div className="flex space-x-4">
                  <Button onClick={criarCesta} disabled={loading || !novaCesta || !modeloSelecionado}>
                    {loading ? 'Criando...' : 'Criar Cesta'}
                  </Button>
                  <Button variant="outline" onClick={carregarCestas}>
                    Atualizar Lista
                  </Button>
                </div>

                <div className="border rounded-lg p-6">
                  {cestas.length === 0 ? (
                    <div className="text-center py-8">
                      <Package className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                      <p className="text-gray-500">Nenhuma cesta básica encontrada.</p>
                      <p className="text-gray-400 text-sm">Crie uma nova cesta para começar.</p>
                    </div>
                  ) : (
                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                      {cestas.map((cesta, index) => (
                        <div key={index} className="border rounded-lg p-4">
                          <h3 className="font-medium">{cesta.nomeCesta || cesta.nome || `Cesta ${index + 1}`}</h3>
                          <p className="text-sm text-gray-500">Status: {cesta.status || 'Ativa'}</p>
                          <p className="text-sm text-gray-500">Data: {cesta.dataMontagem || new Date().toLocaleDateString()}</p>
                          {cesta.preDefinicao && (
                            <p className="text-sm text-blue-600">Modelo: {cesta.preDefinicao.nome}</p>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Aba Modelos de Cestas */}
          <TabsContent value="modelos">
            <Card>
              <CardHeader>
                <CardTitle>Gerenciar Modelos de Cestas</CardTitle>
                <CardDescription>Crie e gerencie modelos pré-definidos para cestas básicas</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="grid gap-4 md:grid-cols-2">
                  <div>
                    <Label htmlFor="nome-modelo">Nome do Modelo *</Label>
                    <Input
                      id="nome-modelo"
                      placeholder="Ex: Cesta Familiar"
                      value={novoModelo.nome}
                      onChange={(e) => setNovoModelo({...novoModelo, nome: e.target.value})}
                    />
                  </div>
                  <div>
                    <Label htmlFor="descricao-modelo">Descrição</Label>
                    <Input
                      id="descricao-modelo"
                      placeholder="Ex: Cesta para família de 4 pessoas"
                      value={novoModelo.descricao}
                      onChange={(e) => setNovoModelo({...novoModelo, descricao: e.target.value})}
                    />
                  </div>
                </div>

                {/* Adicionar itens ao modelo */}
                <div className="border rounded-lg p-4 space-y-4">
                  <h4 className="font-medium">Adicionar Itens ao Modelo</h4>
                  <div className="grid gap-4 md:grid-cols-4">
                    <div>
                      <Label htmlFor="tipo-alimento">Tipo de Alimento *</Label>
                      <Input
                        id="tipo-alimento"
                        placeholder="Ex: Arroz"
                        value={itemModelo.tipoAlimento}
                        onChange={(e) => setItemModelo({...itemModelo, tipoAlimento: e.target.value})}
                      />
                    </div>
                    <div>
                      <Label htmlFor="quantidade">Quantidade *</Label>
                      <Input
                        id="quantidade"
                        type="number"
                        step="0.1"
                        placeholder="Ex: 5"
                        value={itemModelo.quantidade}
                        onChange={(e) => setItemModelo({...itemModelo, quantidade: e.target.value})}
                      />
                    </div>
                    <div>
                      <Label htmlFor="unidade">Unidade</Label>
                      <Select value={itemModelo.unidadeMedida} onValueChange={(value) => setItemModelo({...itemModelo, unidadeMedida: value})}>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="kg">kg</SelectItem>
                          <SelectItem value="litro">litro</SelectItem>
                          <SelectItem value="unidade">unidade</SelectItem>
                          <SelectItem value="pacote">pacote</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="flex items-end">
                      <Button onClick={adicionarItemModelo} disabled={!itemModelo.tipoAlimento || !itemModelo.quantidade}>
                        Adicionar
                      </Button>
                    </div>
                  </div>

                  {/* Lista de itens do modelo */}
                  {novoModelo.itens.length > 0 && (
                    <div className="space-y-2">
                      <h5 className="font-medium text-sm">Itens do Modelo:</h5>
                      {novoModelo.itens.map((item, index) => (
                        <div key={index} className="flex justify-between items-center p-2 bg-gray-50 rounded">
                          <span>{item.tipoAlimento} - {item.quantidade} {item.unidadeMedida}</span>
                          <Button variant="ghost" size="sm" onClick={() => removerItemModelo(index)}>
                            <Trash2 className="w-4 h-4" />
                          </Button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                <div className="flex space-x-4">
                  <Button onClick={criarModelo} disabled={loading || !novoModelo.nome || novoModelo.itens.length === 0}>
                    {loading ? 'Criando...' : 'Criar Modelo'}
                  </Button>
                  <Button variant="outline" onClick={carregarModelos}>
                    Atualizar Lista
                  </Button>
                </div>

                {/* Lista de modelos existentes */}
                <div className="border rounded-lg p-6">
                  {modelos.length === 0 ? (
                    <div className="text-center py-8">
                      <Settings className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                      <p className="text-gray-500">Nenhum modelo encontrado.</p>
                      <p className="text-gray-400 text-sm">Crie um novo modelo para começar.</p>
                    </div>
                  ) : (
                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                      {modelos.map((modelo) => (
                        <div key={modelo.id} className="border rounded-lg p-4">
                          <h3 className="font-medium">{modelo.nome}</h3>
                          <p className="text-sm text-gray-500">{modelo.descricao}</p>
                          <p className="text-sm text-blue-600">{modelo.itens?.length || 0} itens</p>
                          {modelo.itens && modelo.itens.length > 0 && (
                            <div className="mt-2 text-xs text-gray-500">
                              {modelo.itens.slice(0, 3).map((item, idx) => (
                                <div key={idx}>{item.tipoAlimento} ({item.quantidade} {item.unidadeMedida})</div>
                              ))}
                              {modelo.itens.length > 3 && <div>... e mais {modelo.itens.length - 3} itens</div>}
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Aba Beneficiados */}
          <TabsContent value="beneficiados">
            <Card>
              <CardHeader>
                <CardTitle>Gerenciar Beneficiados</CardTitle>
                <CardDescription>Cadastre e gerencie os beneficiados do programa</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="grid gap-4 md:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="nome">Nome *</Label>
                    <Input
                      id="nome"
                      placeholder="Nome completo"
                      value={novoBeneficiado.nome}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, nome: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="cpf">CPF *</Label>
                    <Input
                      id="cpf"
                      placeholder="000.000.000-00"
                      value={novoBeneficiado.cpf}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, cpf: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="telefone">Telefone</Label>
                    <Input
                      id="telefone"
                      placeholder="(00) 00000-0000"
                      value={novoBeneficiado.telefone}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, telefone: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="dataNascimento">Data de Nascimento</Label>
                    <Input
                      id="dataNascimento"
                      type="date"
                      value={novoBeneficiado.dataNascimento}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, dataNascimento: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="rendaFamiliar">Renda Familiar</Label>
                    <Input
                      id="rendaFamiliar"
                      type="number"
                      placeholder="0.00"
                      value={novoBeneficiado.rendaFamiliar}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, rendaFamiliar: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="numeroDependentes">Número de Dependentes</Label>
                    <Input
                      id="numeroDependentes"
                      type="number"
                      placeholder="0"
                      value={novoBeneficiado.numeroDependentes}
                      onChange={(e) => setNovoBeneficiado({...novoBeneficiado, numeroDependentes: e.target.value})}
                    />
                  </div>
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="endereco">Endereço</Label>
                  <Textarea
                    id="endereco"
                    placeholder="Endereço completo"
                    value={novoBeneficiado.endereco}
                    onChange={(e) => setNovoBeneficiado({...novoBeneficiado, endereco: e.target.value})}
                  />
                </div>

                <div className="flex space-x-4">
                  <Button onClick={criarBeneficiado} disabled={loading}>
                    {loading ? 'Cadastrando...' : 'Cadastrar Beneficiado'}
                  </Button>
                  <Button variant="outline" onClick={carregarBeneficiados}>
                    Atualizar Lista
                  </Button>
                </div>

                <div className="border rounded-lg p-6">
                  {beneficiados.length === 0 ? (
                    <div className="text-center py-8">
                      <Users className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                      <p className="text-gray-500">Nenhum beneficiado cadastrado.</p>
                      <p className="text-gray-400 text-sm">Cadastre um novo beneficiado para começar.</p>
                    </div>
                  ) : (
                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                      {beneficiados.map((beneficiado, index) => (
                        <div key={index} className="border rounded-lg p-4">
                          <h3 className="font-medium">{beneficiado.nome}</h3>
                          <p className="text-sm text-gray-500">CPF: {beneficiado.cpf}</p>
                          <p className="text-sm text-gray-500">Telefone: {beneficiado.telefone || 'Não informado'}</p>
                          <p className="text-sm text-gray-500">Dependentes: {beneficiado.numeroDependentes || 0}</p>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Aba Doações */}
          <TabsContent value="doacoes">
            <Card>
              <CardHeader>
                <CardTitle>Registrar Doações</CardTitle>
                <CardDescription>Registre e gerencie doações recebidas</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="grid gap-4 md:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="tipoAlimento">Tipo de Alimento *</Label>
                    <Input
                      id="tipoAlimento"
                      placeholder="Ex: Arroz, Feijão, Óleo"
                      value={novaDoacao.tipoAlimento}
                      onChange={(e) => setNovaDoacao({...novaDoacao, tipoAlimento: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="quantidade">Quantidade *</Label>
                    <div className="flex space-x-2">
                      <Input
                        id="quantidade"
                        type="number"
                        placeholder="0.00"
                        value={novaDoacao.quantidade}
                        onChange={(e) => setNovaDoacao({...novaDoacao, quantidade: e.target.value})}
                        className="flex-1"
                      />
                      <Button variant="outline" disabled>kg</Button>
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="dataRecebimento">Data de Recebimento</Label>
                    <Input
                      id="dataRecebimento"
                      type="date"
                      value={novaDoacao.dataRecebimento}
                      onChange={(e) => setNovaDoacao({...novaDoacao, dataRecebimento: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="dataValidade">Data de Validade</Label>
                    <Input
                      id="dataValidade"
                      type="date"
                      value={novaDoacao.dataValidade}
                      onChange={(e) => setNovaDoacao({...novaDoacao, dataValidade: e.target.value})}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="origem">Origem da Doação</Label>
                    <Input
                      id="origem"
                      placeholder="Ex: Supermercado X, Pessoa Física"
                      value={novaDoacao.origem}
                      onChange={(e) => setNovaDoacao({...novaDoacao, origem: e.target.value})}
                    />
                  </div>
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="observacoes">Observações</Label>
                  <Textarea
                    id="observacoes"
                    placeholder="Observações adicionais"
                    value={novaDoacao.observacoes}
                    onChange={(e) => setNovaDoacao({...novaDoacao, observacoes: e.target.value})}
                  />
                </div>

                <div className="flex space-x-4">
                  <Button onClick={criarDoacao} disabled={loading}>
                    {loading ? 'Registrando...' : 'Registrar Doação'}
                  </Button>
                  <Button variant="outline" onClick={carregarDoacoes}>
                    Atualizar Lista
                  </Button>
                </div>

                <div className="border rounded-lg p-6">
                  {doacoes.length === 0 ? (
                    <div className="text-center py-8">
                      <ShoppingCart className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                      <p className="text-gray-500">Nenhuma doação registrada.</p>
                      <p className="text-gray-400 text-sm">Registre uma nova doação para começar.</p>
                    </div>
                  ) : (
                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                      {doacoes.map((doacao, index) => (
                        <div key={index} className="border rounded-lg p-4">
                          <h3 className="font-medium">{doacao.tipoAlimento}</h3>
                          <p className="text-sm text-gray-500">Quantidade: {doacao.quantidade} kg</p>
                          <p className="text-sm text-gray-500">Origem: {doacao.origem || 'Não informado'}</p>
                          <p className="text-sm text-gray-500">Recebimento: {doacao.dataRecebimento || 'Não informado'}</p>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Aba Alertas */}
          <TabsContent value="alertas">
            <div className="space-y-6">
              {/* Dashboard de Alertas */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center space-x-2">
                    <Bell className="w-5 h-5" />
                    <span>Dashboard de Alertas</span>
                    <Button 
                      variant="outline" 
                      size="sm" 
                      onClick={carregarAlertas}
                      disabled={loadingAlertas}
                    >
                      {loadingAlertas ? 'Atualizando...' : 'Atualizar'}
                    </Button>
                  </CardTitle>
                  <CardDescription>
                    Monitoramento em tempo real de alertas do sistema
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {alertas.dashboard ? (
                    <div className="grid gap-4 md:grid-cols-4">
                      <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
                        <div className="text-2xl font-bold text-red-600">
                          {alertas.dashboard.alertasCriticos}
                        </div>
                        <div className="text-sm text-red-800">Alertas Críticos</div>
                      </div>
                      <div className="p-4 bg-orange-50 border border-orange-200 rounded-lg">
                        <div className="text-2xl font-bold text-orange-600">
                          {alertas.dashboard.alertasAltos}
                        </div>
                        <div className="text-sm text-orange-800">Alertas Altos</div>
                      </div>
                      <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                        <div className="text-2xl font-bold text-yellow-600">
                          {alertas.dashboard.totalAlertasVencimento}
                        </div>
                        <div className="text-sm text-yellow-800">Próximos ao Vencimento</div>
                      </div>
                      <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
                        <div className="text-2xl font-bold text-blue-600">
                          {alertas.dashboard.totalAlertas}
                        </div>
                        <div className="text-sm text-blue-800">Total de Alertas</div>
                      </div>
                    </div>
                  ) : (
                    <div className="text-center py-8">
                      <div className="text-gray-500">Carregando dashboard de alertas...</div>
                    </div>
                  )}
                </CardContent>
              </Card>

              {/* Alertas de Vencimento */}
              <Card>
                <CardHeader>
                  <CardTitle>Produtos Próximos ao Vencimento</CardTitle>
                  <CardDescription>
                    Alimentos que vencem nos próximos 30 dias
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {alertas.vencimento.length > 0 ? (
                    <div className="space-y-3">
                      {alertas.vencimento.map((alerta, index) => (
                        <div 
                          key={index}
                          className={`p-4 border rounded-lg ${
                            alerta.prioridade === 'CRÍTICO' ? 'bg-red-50 border-red-200' :
                            alerta.prioridade === 'ALTO' ? 'bg-orange-50 border-orange-200' :
                            'bg-yellow-50 border-yellow-200'
                          }`}
                        >
                          <div className="flex justify-between items-start">
                            <div>
                              <div className="font-medium">{alerta.tipoAlimento}</div>
                              <div className="text-sm text-gray-600">{alerta.mensagem}</div>
                              <div className="text-xs text-gray-500 mt-1">
                                Local: {alerta.localArmazenamento} | Quantidade: {alerta.quantidadeDisponivel}
                              </div>
                            </div>
                            <Badge 
                              variant={
                                alerta.prioridade === 'CRÍTICO' ? 'destructive' :
                                alerta.prioridade === 'ALTO' ? 'default' : 'secondary'
                              }
                            >
                              {alerta.prioridade}
                            </Badge>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-8 text-gray-500">
                      Nenhum produto próximo ao vencimento
                    </div>
                  )}
                </CardContent>
              </Card>

              {/* Alertas de Estoque Baixo */}
              <Card>
                <CardHeader>
                  <CardTitle>Estoque Baixo</CardTitle>
                  <CardDescription>
                    Produtos com estoque abaixo do limite mínimo
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {alertas.estoqueBaixo.length > 0 ? (
                    <div className="space-y-3">
                      {alertas.estoqueBaixo.map((alerta, index) => (
                        <div key={index} className="p-4 bg-orange-50 border border-orange-200 rounded-lg">
                          <div className="flex justify-between items-start">
                            <div>
                              <div className="font-medium">{alerta.tipoAlimento}</div>
                              <div className="text-sm text-gray-600">{alerta.mensagem}</div>
                            </div>
                            <Badge variant="default">{alerta.prioridade}</Badge>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-8 text-gray-500">
                      Todos os produtos têm estoque adequado
                    </div>
                  )}
                </CardContent>
              </Card>

              {/* Alertas de Beneficiados */}
              <Card>
                <CardHeader>
                  <CardTitle>Beneficiados Inativos</CardTitle>
                  <CardDescription>
                    Beneficiados que não recebem doações há mais de 90 dias
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {alertas.beneficiados.length > 0 ? (
                    <div className="space-y-3">
                      {alertas.beneficiados.map((alerta, index) => (
                        <div key={index} className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
                          <div className="flex justify-between items-start">
                            <div>
                              <div className="font-medium">{alerta.nome}</div>
                              <div className="text-sm text-gray-600">{alerta.mensagem}</div>
                              <div className="text-xs text-gray-500 mt-1">
                                CPF: {alerta.cpf} | Telefone: {alerta.telefone}
                              </div>
                            </div>
                            <Badge variant="secondary">{alerta.prioridade}</Badge>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-8 text-gray-500">
                      Todos os beneficiados estão ativos
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          {/* Aba Relatórios */}
          <TabsContent value="relatorios">
            <div className="grid gap-6 md:grid-cols-2">
              <Card>
                <CardHeader>
                  <CardTitle>Resumo Geral</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex justify-between">
                    <span>Total de Cestas:</span>
                    <Badge variant="secondary">{stats.totalCestas}</Badge>
                  </div>
                  <div className="flex justify-between">
                    <span>Total de Beneficiados:</span>
                    <Badge variant="secondary">{stats.totalBeneficiados}</Badge>
                  </div>
                  <div className="flex justify-between">
                    <span>Total de Doações:</span>
                    <Badge variant="secondary">{stats.totalDoacoes}</Badge>
                  </div>
                  {userRole === 'ADMIN' && (
                    <div className="flex justify-between">
                      <span>Total de Usuários:</span>
                      <Badge variant="secondary">{stats.totalUsuarios}</Badge>
                    </div>
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Ações Rápidas</CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  <Button 
                    variant="outline" 
                    className="w-full justify-start"
                    onClick={() => exportarRelatorio('beneficiados')}
                    disabled={loading}
                  >
                    Exportar Relatório de Beneficiados
                  </Button>
                  <Button 
                    variant="outline" 
                    className="w-full justify-start"
                    onClick={() => exportarRelatorio('doacoes')}
                    disabled={loading}
                  >
                    Exportar Relatório de Doações
                  </Button>
                  <Button 
                    variant="outline" 
                    className="w-full justify-start"
                    onClick={() => exportarRelatorio('distribuicoes')}
                    disabled={loading}
                  >
                    Relatório de Distribuições
                  </Button>
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          {/* Aba Usuários (apenas para admin) */}
          {userRole === 'ADMIN' && (
            <TabsContent value="usuarios">
              <Card>
                <CardHeader>
                  <CardTitle>Gerenciar Usuários</CardTitle>
                  <CardDescription>Crie e gerencie usuários do sistema (apenas administradores)</CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                  <div className="grid gap-4 md:grid-cols-2">
                    <div className="space-y-2">
                      <Label htmlFor="username">Nome de Usuário *</Label>
                      <Input
                        id="username"
                        placeholder="nome_usuario"
                        value={novoUsuario.username}
                        onChange={(e) => setNovoUsuario({...novoUsuario, username: e.target.value})}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="password">Senha *</Label>
                      <Input
                        id="password"
                        type="password"
                        placeholder="Senha segura"
                        value={novoUsuario.password}
                        onChange={(e) => setNovoUsuario({...novoUsuario, password: e.target.value})}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="email">Email</Label>
                      <Input
                        id="email"
                        type="email"
                        placeholder="usuario@email.com"
                        value={novoUsuario.email}
                        onChange={(e) => setNovoUsuario({...novoUsuario, email: e.target.value})}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="role">Função</Label>
                      <Select value={novoUsuario.role} onValueChange={(value) => setNovoUsuario({...novoUsuario, role: value})}>
                        <SelectTrigger>
                          <SelectValue placeholder="Selecione a função" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="USER">Usuário</SelectItem>
                          <SelectItem value="ADMIN">Administrador</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  </div>

                  <div className="flex space-x-4">
                    <Button onClick={criarUsuario} disabled={loading}>
                      {loading ? 'Criando...' : 'Criar Usuário'}
                    </Button>
                    <Button variant="outline" onClick={carregarUsuarios}>
                      Atualizar Lista
                    </Button>
                  </div>

                  <div className="border rounded-lg p-6">
                    {usuarios.length === 0 ? (
                      <div className="text-center py-8">
                        <UserPlus className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                        <p className="text-gray-500">Nenhum usuário encontrado.</p>
                        <p className="text-gray-400 text-sm">Crie um novo usuário para começar.</p>
                      </div>
                    ) : (
                      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                        {usuarios.map((usuario, index) => (
                          <div key={index} className="border rounded-lg p-4">
                            <h3 className="font-medium">{usuario.username}</h3>
                            <p className="text-sm text-gray-500">Email: {usuario.email || 'Não informado'}</p>
                            <Badge variant={usuario.role === 'ADMIN' ? 'default' : 'secondary'}>
                              {usuario.role}
                            </Badge>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
          )}
        </Tabs>
      </main>
    </div>
  )
}

export default App

